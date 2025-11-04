package personalfinancemanager.view.gui;

import java.awt.*;
import javax.swing.*;
// **** THIS IS THE FIX ****
// The package is javax.swing.filechooser, not javax.swing.file
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
// End of new imports

import personalfinancemanager.auth.Session;
import personalfinancemanager.service.FinanceService;
// Import the ExportService
import personalfinancemanager.service.ExportService;

public class DashboardPanel extends JPanel {

    private final MainFrame mainFrame;
    private final FinanceService financeService;
    private final JPanel contentPanel; // Make contentPanel a field

    // We need an ExportService instance
    private final ExportService exportService;

    public DashboardPanel(MainFrame mainFrame, FinanceService financeService) {
        this.mainFrame = mainFrame;
        this.financeService = financeService;

        // Instantiate the ExportService
        this.exportService = new ExportService(financeService);

        setLayout(new BorderLayout());

        // === 1. WELCOME MESSAGE ===
        String username = Session.getUser() != null ? Session.getUser().getUsername() : "User";
        JLabel welcomeLabel = new JLabel("Welcome, " + username, SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(welcomeLabel, BorderLayout.NORTH);

        // === 2. MAIN CONTENT AREA (WHERE OTHER PANELS WILL LOAD) ===
        contentPanel = new JPanel(new BorderLayout()); // Use BorderLayout
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(contentPanel, BorderLayout.CENTER);

        // === 3. NAVIGATION PANEL (BUTTONS) ===
        // Use a 2-column grid to make it look cleaner
        JPanel navPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Create all buttons based on ConsoleUI ---
        JButton viewTransactionsButton = new JButton("View Transactions");
        JButton addTransactionButton = new JButton("Add Transaction");
        JButton editTransactionButton = new JButton("Edit Transaction");
        JButton deleteTransactionButton = new JButton("Delete Transaction");
        JButton manageAccountsButton = new JButton("Manage Accounts");
        JButton manageCategoriesButton = new JButton("Manage Categories");
        JButton setBudgetButton = new JButton("Set Monthly Budget");
        JButton checkBudgetButton = new JButton("Check Budget Status");
        JButton monthlyReportButton = new JButton("Monthly Category Report");
        JButton topSpendingButton = new JButton("Top Spending Report");
        JButton netSavingsButton = new JButton("View Net Savings");
        JButton exportAllButton = new JButton("Export All Transactions (CSV)");
        JButton exportSummaryButton = new JButton("Export Monthly Summary");
        JButton logoutButton = new JButton("Logout");

        // --- Add all buttons to the nav panel ---
        navPanel.add(viewTransactionsButton);
        navPanel.add(addTransactionButton);
        navPanel.add(editTransactionButton);
        navPanel.add(deleteTransactionButton);
        navPanel.add(manageAccountsButton);
        navPanel.add(manageCategoriesButton);
        navPanel.add(setBudgetButton);
        navPanel.add(checkBudgetButton);
        navPanel.add(monthlyReportButton);
        navPanel.add(topSpendingButton);
        navPanel.add(netSavingsButton);
        navPanel.add(exportAllButton);
        navPanel.add(exportSummaryButton);
        navPanel.add(logoutButton);

        add(navPanel, BorderLayout.WEST);

        // === 4. ACTION LISTENERS (This is the fix) ===

        // This one was already working
        viewTransactionsButton.addActionListener(e -> {
            TransactionPanel txPanel = new TransactionPanel(financeService);
            showPanel(txPanel);
        });

        addTransactionButton.addActionListener(e -> {
            AddTransactionPanel addTxPanel = new AddTransactionPanel(financeService);
            showPanel(addTxPanel);
        });

        // ADDED: Listener for Edit Transaction
        editTransactionButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Edit Transaction panel not yet implemented.");
        });

        // ADDED: Listener for Delete Transaction
        deleteTransactionButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Delete Transaction panel not yet implemented.");
        });

        manageAccountsButton.addActionListener(e -> {
            ManageAccountsPanel accountsPanel = new ManageAccountsPanel(financeService);
            showPanel(accountsPanel);
        });

        manageCategoriesButton.addActionListener(e -> {
            ManageCategoriesPanel catPanel = new ManageCategoriesPanel(financeService);
            showPanel(catPanel);
        });

        setBudgetButton.addActionListener(e -> {
            SetBudgetPanel budgetPanel = new SetBudgetPanel(financeService);
            showPanel(budgetPanel);
        });

        checkBudgetButton.addActionListener(e -> {
            CheckBudgetPanel checkBudget = new CheckBudgetPanel(financeService);
            showPanel(checkBudget);
        });

        monthlyReportButton.addActionListener(e -> {
            MonthlyReportPanel reportPanel = new MonthlyReportPanel(financeService);
            showPanel(reportPanel);
        });

        topSpendingButton.addActionListener(e -> {
            TopSpendingPanel topSpendingPanel = new TopSpendingPanel(financeService);
            showPanel(topSpendingPanel);
        });


        netSavingsButton.addActionListener(e -> {
            NetSavingsPanel netSavingsPanel = new NetSavingsPanel(financeService);
            showPanel(netSavingsPanel);
        });

        // **** THIS IS THE NEWLY IMPLEMENTED BUTTON ****
        exportAllButton.addActionListener(e -> {
            exportAllTransactions();
        });

        exportSummaryButton.addActionListener(e -> {
            // This creates and shows your new panel
            ExportSummaryPanel summaryPanel = new ExportSummaryPanel(financeService, exportService);
            showPanel(summaryPanel);
        });

        // This one was already working
        logoutButton.addActionListener(e -> {
            Session.logout();
            mainFrame.switchToPanel("LOGIN");
        });
    }

    /**
     * Helper method to clear the content panel and show a new one.
     * @param panel The panel to display.
     */
    private void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * New method to handle the "Export All" logic
     */
    private void exportAllTransactions() {
        // 1. Create a file chooser dialog
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Transactions As...");

        // 2. Set a default filename
        String defaultFilename = "transactions_" + Session.getUser().getUsername() + ".csv";
        fileChooser.setSelectedFile(new File(defaultFilename));

        // 3. Filter for .csv files
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV File (*.csv)", "csv"));

        // 4. Show the dialog
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Ensure the file has a .csv extension
            if (!fileToSave.getAbsolutePath().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }

            try {
                // 5. Call our new, upgraded export method
                int userId = Session.getUser().getUserId();
                boolean success = exportService.exportAllTransactions(userId, fileToSave);

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Transactions exported successfully to:\n" + fileToSave.getAbsolutePath(),
                            "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No transactions found to export.",
                            "Export Info", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "An error occurred while exporting the file:\n" + ex.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

