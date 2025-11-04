package personalfinancemanager.view.gui;

import java.awt.*;
import javax.swing.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import personalfinancemanager.auth.Session;
import personalfinancemanager.service.FinanceService;

public class TopSpendingPanel extends JPanel {

    private final FinanceService financeService;
    private final JTextField yearField;
    private final JComboBox<String> monthComboBox;
    private final JTextArea resultArea; // Use a text area for simple list

    public TopSpendingPanel(FinanceService financeService) {
        this.financeService = financeService;
        setLayout(new BorderLayout(10, 10)); // Gaps
        setBorder(BorderFactory.createTitledBorder("Top 3 Spending Categories"));

        // --- 1. Input Panel (Top) ---
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Year:"));
        yearField = new JTextField(String.valueOf(LocalDate.now().getYear()), 6);
        inputPanel.add(yearField);

        inputPanel.add(new JLabel("Month:"));
        String[] months = {
                "January (1)", "February (2)", "March (3)", "April (4)", "May (5)", "June (6)",
                "July (7)", "August (8)", "September (9)", "October (10)", "November (11)", "December (12)"
        };
        monthComboBox = new JComboBox<>(months);
        monthComboBox.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        inputPanel.add(monthComboBox);

        JButton generateButton = new JButton("Generate Report");
        inputPanel.add(generateButton);

        add(inputPanel, BorderLayout.NORTH);

        // --- 2. Result Display (Center) ---
        resultArea = new JTextArea();
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        resultArea.setEditable(false);
        resultArea.setMargin(new Insets(10, 10, 10, 10)); // Padding

        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // --- Action Listener ---
        generateButton.addActionListener(e -> generateReport());

        // Load the report for the current month by default
        generateReport();
    }

    private void generateReport() {
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

            // 3. Call the service
            int userId = Session.getUser().getUserId();
            // This calls the getTopCategories method from your FinanceService
            List<Map.Entry<String, Double>> topCategories = financeService.getTopCategories(userId, year, month);

            // 4. Format and display the results
            StringBuilder reportText = new StringBuilder();
            if (topCategories.isEmpty()) {
                reportText.append("No spending data found for this month.");
            } else {
                reportText.append(String.format("Top Spending for %s %d:\n\n",
                        monthComboBox.getSelectedItem().toString().split(" ")[0], year));

                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new java.util.Locale("en", "IN"));
                int rank = 1;
                for (Map.Entry<String, Double> entry : topCategories) {
                    // Format into a neat, aligned string
                    reportText.append(String.format("%d. %-25s %s\n",
                            rank++,
                            entry.getKey(),
                            currencyFormat.format(entry.getValue())));
                }
            }

            resultArea.setText(reportText.toString());

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
