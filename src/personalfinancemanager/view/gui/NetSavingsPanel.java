package personalfinancemanager.view.gui;

import java.awt.*;
import javax.swing.*;
import java.text.NumberFormat;
import java.util.Locale;
import java.time.LocalDate;
import personalfinancemanager.auth.Session;
import personalfinancemanager.service.FinanceService;

public class NetSavingsPanel extends JPanel {

    private final FinanceService financeService;
    private final JLabel savingsLabel;
    private final JComboBox<String> monthComboBox;
    private final JTextField yearField;

    public NetSavingsPanel(FinanceService financeService) {
        this.financeService = financeService;

        setLayout(new BorderLayout(10, 10)); // Gaps
        setBorder(BorderFactory.createTitledBorder("Net Savings Report"));

        // --- 1. Input Panel (Top) ---
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Year:"));
        yearField = new JTextField(String.valueOf(LocalDate.now().getYear()), 6);
        inputPanel.add(yearField);

        inputPanel.add(new JLabel("Month:"));
        String[] months = {
                "All-Time", // <-- Our new option
                "January (1)", "February (2)", "March (3)", "April (4)", "May (5)", "June (6)",
                "July (7)", "August (8)", "September (9)", "October (10)", "November (11)", "December (12)"
        };
        monthComboBox = new JComboBox<>(months);
        // Select current month by default
        monthComboBox.setSelectedIndex(LocalDate.now().getMonthValue());
        inputPanel.add(monthComboBox);

        JButton generateButton = new JButton("Generate Report");
        inputPanel.add(generateButton);

        add(inputPanel, BorderLayout.NORTH);

        // --- 2. Result Display (Center) ---
        // Use a wrapper panel to center the label
        JPanel centerPanel = new JPanel(new GridBagLayout());

        savingsLabel = new JLabel("Click 'Generate Report' to see savings.", SwingConstants.CENTER);
        savingsLabel.setFont(new Font("Arial", Font.BOLD, 28));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 20, 20, 20);
        centerPanel.add(savingsLabel, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // --- Action Listener ---
        generateButton.addActionListener(e -> generateReport());

        // Load the report for the current month by default
        generateReport();
    }

    private void generateReport() {
        try {
            int userId = Session.getUser().getUserId();
            double netSavings = 0.0;

            int selectedMonthIndex = monthComboBox.getSelectedIndex();

            if (selectedMonthIndex == 0) { // "All-Time"
                // This is the original method call
                netSavings = financeService.getNetSavings(userId);
            } else {
                // Monthly calculation
                int month = selectedMonthIndex; // Index 1 is month 1 (Jan)
                int year;
                try {
                    year = Integer.parseInt(yearField.getText());
                    if (year < 2000 || year > 2100) throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid year. Please enter a valid 4-digit year.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // **** This is the NEW method we will add to FinanceService ****
                netSavings = financeService.getNetSavingsByMonth(userId, year, month);
            }

            // --- Display the result (same as before) ---
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            String savingsText = currencyFormat.format(netSavings);

            String htmlText;
            if (netSavings >= 0) {
                // Display in green
                htmlText = String.format(
                        "<html>Your Net Savings are:<br><center><font color='#008800'>%s</font></center></html>",
                        savingsText
                );
            } else {
                // Display in red
                htmlText = String.format(
                        "<html>Your Net Deficit is:<br><center><font color='#D80000'>%s</font></center></html>",
                        savingsText
                );
            }
            savingsLabel.setText(htmlText);

        } catch (Exception e) {
            e.printStackTrace();
            savingsLabel.setText("<html><font color='red'>Error loading savings.</font></html>");
            JOptionPane.showMessageDialog(this, "Error loading net savings: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}