package personalfinancemanager.view.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.time.LocalDate;
import personalfinancemanager.auth.Session;
import personalfinancemanager.service.ExportService;
import personalfinancemanager.service.FinanceService;

public class ExportSummaryPanel extends JPanel {

    private final FinanceService financeService;
    private final ExportService exportService;
    private final JComboBox<String> monthComboBox;
    private final JTextField yearField;

    public ExportSummaryPanel(FinanceService financeService, ExportService exportService) {
        this.financeService = financeService;
        this.exportService = exportService;

        setBorder(BorderFactory.createTitledBorder("Export Monthly Summary"));
        setLayout(new FlowLayout(FlowLayout.LEFT));

        // --- 1. Input Panel ---
        add(new JLabel("Year:"));
        yearField = new JTextField(String.valueOf(LocalDate.now().getYear()), 6);
        add(yearField);

        add(new JLabel("Month:"));
        String[] months = {
                "January (1)", "February (2)", "March (3)", "April (4)", "May (5)", "June (6)",
                "July (7)", "August (8)", "September (9)", "October (10)", "November (11)", "December (12)"
        };
        monthComboBox = new JComboBox<>(months);
        monthComboBox.setSelectedIndex(LocalDate.now().getMonthValue() - 1); // Select current month
        add(monthComboBox);

        JButton generateButton = new JButton("Generate Export...");
        add(generateButton);

        // --- Action Listener ---
        generateButton.addActionListener(e -> generateExport());
    }

    private void generateExport() {
        try {
            // 1. Get and validate inputs
            int year;
            try {
                year = Integer.parseInt(yearField.getText());
                if (year < 2000 || year > 2100) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid year. Please enter a 4-digit year (e.g., 2025).", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get month (index 0 is Jan, so we add 1)
            int month = monthComboBox.getSelectedIndex() + 1;

            // 2. Create and configure "Save As..." dialog
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Monthly Summary As...");

            // Set default filename
            String defaultFilename = String.format("summary_%s_%d_%02d.csv",
                    Session.getUser().getUsername(), year, month);
            fileChooser.setSelectedFile(new File(defaultFilename));

            // Filter for .csv files
            fileChooser.setFileFilter(new FileNameExtensionFilter("CSV File (*.csv)", "csv"));

            // 3. Show the dialog
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                // Ensure the file has a .csv extension
                if (!fileToSave.getAbsolutePath().endsWith(".csv")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
                }

                // 4. Call the export method
                int userId = Session.getUser().getUserId();

                // This calls the new method in ExportService.java
                boolean success = exportService.exportMonthlySummary(userId, year, month, fileToSave);

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Summary exported successfully to:\n" + fileToSave.getAbsolutePath(),
                            "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No data found for the selected month.",
                            "Export Info", JOptionPane.INFORMATION_MESSAGE);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "An error occurred while exporting the file:\n" + ex.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

