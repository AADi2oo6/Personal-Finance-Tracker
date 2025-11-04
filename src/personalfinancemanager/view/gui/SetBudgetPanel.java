package personalfinancemanager.view.gui;

import java.awt.*;
import javax.swing.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import personalfinancemanager.auth.Session;
import personalfinancemanager.service.FinanceService;

public class SetBudgetPanel extends JPanel {

    private final FinanceService financeService;
    private final JTextField yearField;
    private final JComboBox<String> monthComboBox;
    private final JTextField amountField;

    public SetBudgetPanel(FinanceService financeService) {
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
        JLabel titleLabel = new JLabel("Set Monthly Budget");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, gbc);

        // --- Row 1: Year ---
        gbc.gridwidth = 1; // Reset gridwidth
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Year:"), gbc);

        gbc.gridx = 1;
        // Pre-fill with the current year
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
        // Pre-select the current month
        monthComboBox.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        add(monthComboBox, gbc);

        // --- Row 3: Amount ---
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Budget Amount:"), gbc);

        gbc.gridx = 1;
        amountField = new JTextField(15);
        add(amountField, gbc);

        // --- Row 4: Save Button ---
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton saveButton = new JButton("Set Budget");
        add(saveButton, gbc);

        // --- Action Listener ---
        saveButton.addActionListener(e -> setBudget());
    }

    private void setBudget() {
        try {
            // 1. Get and validate Year
            int year;
            try {
                year = Integer.parseInt(yearField.getText());

                // *** THIS IS THE FIX ***
                // Add a check to ensure the year is reasonable
                if (year < 2000 || year > 2100) {
                    throw new NumberFormatException("Year must be between 2000 and 2100.");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid year. Please enter a valid 4-digit year (e.g., 2025).", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Get Month (index + 1)
            int month = monthComboBox.getSelectedIndex() + 1;

            // 3. Get and validate Amount
            double amount;
            try {
                amount = new BigDecimal(amountField.getText()).doubleValue();
                if (amount < 0) throw new NumberFormatException("Amount cannot be negative.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a positive number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 4. Call the service
            int userId = Session.getUser().getUserId();
            boolean success = financeService.setMonthlyBudget(userId, year, month, amount);

            if (success) {
                JOptionPane.showMessageDialog(this, "Budget set successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                amountField.setText(""); // Clear the amount field
            } else {
                throw new Exception("Save operation returned false.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error setting budget: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

