package personalfinancemanager.view.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Map;
import personalfinancemanager.auth.Session;
import personalfinancemanager.service.FinanceService;

public class MonthlyReportPanel extends JPanel {

    private final FinanceService financeService;
    private final JTextField yearField;
    private final JComboBox<String> monthComboBox;
    private final JTable reportTable;
    private final DefaultTableModel tableModel;
    private final JLabel totalLabel;

    public MonthlyReportPanel(FinanceService financeService) {
        this.financeService = financeService;
        setLayout(new BorderLayout(10, 10)); // Gaps
        setBorder(BorderFactory.createTitledBorder("Monthly Category Report"));

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

        // --- 2. Report Table (Center) ---
        String[] columnNames = {"Category", "Amount"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            // Make table cells not editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reportTable = new JTable(tableModel);
        reportTable.setFont(new Font("Arial", Font.PLAIN, 14));
        reportTable.setRowHeight(20);

        // Format the "Amount" column as currency
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new java.util.Locale("en", "IN"));
        reportTable.getColumnModel().getColumn(1).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel(currencyFormat.format(value));
            label.setOpaque(true);
            label.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            label.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            label.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            return label;
        });

        add(new JScrollPane(reportTable), BorderLayout.CENTER);

        // --- 3. Total Label (Bottom) ---
        totalLabel = new JLabel("Total Expenses: ₹0.00", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 10));
        add(totalLabel, BorderLayout.SOUTH);

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
            Map<String, Double> breakdown = financeService.getMonthlyCategoryBreakdown(userId, year, month);

            // 4. Populate the table
            tableModel.setRowCount(0); // Clear old data
            double total = 0.0;

            if (breakdown.isEmpty()) {
                tableModel.addRow(new Object[]{"No expenses found for this month.", 0.0});
            } else {
                for (Map.Entry<String, Double> entry : breakdown.entrySet()) {
                    tableModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
                    total += entry.getValue();
                }
            }

            // 5. Update total label
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new java.util.Locale("en", "IN"));
            totalLabel.setText("Total Expenses: " + currencyFormat.format(total));

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
