package personalfinancemanager.view.gui;

import java.awt.*;
import javax.swing.*;
import java.time.LocalDate;
import java.util.Map;
import personalfinancemanager.auth.Session;
import personalfinancemanager.service.FinanceService;

public class CheckBudgetPanel extends JPanel {

    private final FinanceService financeService;
    private final JTextField yearField;
    private final JComboBox<String> monthComboBox;
    private final JLabel resultLabel; // Label to show the budget status

    public CheckBudgetPanel(FinanceService financeService) {
        this.financeService = financeService;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding
        gbc.anchor = GridBagConstraints.WEST;

        // --- Row 0: Title ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel titleLabel = new JLabel("Check Budget Status");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, gbc);

        // --- Row 1: Year ---
        gbc.gridwidth = 1; // Reset
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Year:"), gbc);

        gbc.gridx = 1;
        yearField = new JTextField(String.valueOf(LocalDate.now().getYear()), 15);
        add(yearField, gbc);

        // --- Row 2: Month ---
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Month:"), gbc);

        gbc.gridx = 1;
        String[] months = {
                "January (1)", "February (2)", "March (3)", "April (4)", "May (5)", "June (6)",
                "July (7)", "August (8)", "September (9)", "October (10)", "November (11)", "December (12)"
        };
        monthComboBox = new JComboBox<>(months);
        monthComboBox.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        add(monthComboBox, gbc);

        // --- Row 3: Check Button ---
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton checkButton = new JButton("Check Status");
        add(checkButton, gbc);

        // --- Row 4: Result Label ---
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        resultLabel = new JLabel(" "); // Start with an empty label
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        resultLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(resultLabel, gbc);

        // --- Action Listener ---
        checkButton.addActionListener(e -> checkBudget());
    }

    private void checkBudget() {
        try {
            // 1. Get and validate Year
            int year;
            try {
                year = Integer.parseInt(yearField.getText());
                if (year < 2000 || year > 2100) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid year. Please enter a valid 4-digit year.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Get Month
            int month = monthComboBox.getSelectedIndex() + 1;
            String monthName = (String) monthComboBox.getSelectedItem();
            monthName = monthName.substring(0, monthName.indexOf(" (")); // Get "January"

            // 3. Call the service
            int userId = Session.getUser().getUserId();
            Double budget = financeService.getMonthlyBudget(userId, year, month);

            if (budget == null) {
                resultLabel.setText("No budget set for " + monthName + " " + year + ".");
                resultLabel.setForeground(Color.BLUE);
                return;
            }

            // 4. Get total spending
            Map<String, Double> breakdown = financeService.getMonthlyCategoryBreakdown(userId, year, month);
            double spent = breakdown.values().stream().mapToDouble(Double::doubleValue).sum();

            // 5. Display the result
            String status;
            Color statusColor;
            double remaining = budget - spent;

            if (spent > budget) {
                status = String.format("Over Budget by ₹%.2f", -remaining);
                statusColor = Color.RED;
            } else {
                status = String.format("Under Budget by ₹%.2f", remaining);
                statusColor = new Color(0, 153, 51); // Dark Green
            }

            String resultText = String.format(
                    "<html><b>Budget for %s %d:</b> ₹%.2f<br>" +
                            "<b>Amount Spent:</b> ₹%.2f<br>" +
                            "<b>Status:</b> %s</html>",
                    monthName, year, budget, spent, status
            );

            resultLabel.setText(resultText);
            resultLabel.setForeground(statusColor);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error checking budget: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
