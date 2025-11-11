package personalfinancemanager.view.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import personalfinancemanager.auth.Session;
import personalfinancemanager.service.ExportService;
import personalfinancemanager.service.FinanceService;
import java.awt.Cursor;
import javax.swing.SwingWorker;

public class DashboardPanel extends JPanel {

    private final MainFrame mainFrame;
    private final FinanceService financeService;
    private final ExportService exportService;
    private final JPanel contentPanel;

    public DashboardPanel(MainFrame mainFrame, FinanceService financeService) {
        this.mainFrame = mainFrame;
        this.financeService = financeService;
        this.exportService = new ExportService(financeService);

        // Use a light background for the whole dashboard
        setBackground(new Color(245, 245, 245));
        setLayout(new BorderLayout());

        // === 1. WELCOME MESSAGE ===
        String username = Session.getUser() != null ? Session.getUser().getUsername() : "User";
        JLabel welcomeLabel = new JLabel("Welcome, " + username, SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(GuiFactory.COLOR_PRIMARY);
        welcomeLabel.setBorder(GuiFactory.BORDER_PANEL_TALL);
        add(welcomeLabel, BorderLayout.NORTH);

        // === 2. MAIN CONTENT AREA (WHERE OTHER PANELS WILL LOAD) ===
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        contentPanel.setOpaque(false); // Make it transparent

        // Add an initial "getting started" panel
        JLabel gettingStartedLabel = new JLabel(
                "<html><center>Select an option from the menu to get started.<br>" +
                        "You can add transactions, manage accounts, or view reports.</center></html>",
                SwingConstants.CENTER
        );
        gettingStartedLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gettingStartedLabel.setForeground(Color.GRAY);
        contentPanel.add(gettingStartedLabel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // === 3. NAVIGATION PANEL (BUTTONS) ===
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(255, 255, 255));
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Create styled buttons using our new factory ---
        JButton addTransactionButton = GuiFactory.createButton("Add Transaction", GuiFactory.COLOR_GREEN);
        JButton viewTransactionsButton = GuiFactory.createButton("View / Edit / Delete", GuiFactory.COLOR_PRIMARY);

        JButton manageAccountsButton = GuiFactory.createButton("Manage Accounts", GuiFactory.COLOR_BLUE);
        JButton manageCategoriesButton = GuiFactory.createButton("Manage Categories", GuiFactory.COLOR_BLUE);
        JButton setBudgetButton = GuiFactory.createButton("Set Monthly Budget", GuiFactory.COLOR_ORANGE);

        JButton checkBudgetButton = GuiFactory.createButton("Check Budget Status", GuiFactory.COLOR_PURPLE);
        JButton monthlyReportButton = GuiFactory.createButton("Monthly Category Report", GuiFactory.COLOR_PURPLE);
        JButton topSpendingButton = GuiFactory.createButton("Top Spending Report", GuiFactory.COLOR_PURPLE);
        JButton netSavingsButton = GuiFactory.createButton("View Net Savings", GuiFactory.COLOR_PURPLE);

        JButton exportAllButton = GuiFactory.createButton("Export All Transactions", GuiFactory.COLOR_GRAY);
        JButton exportSummaryButton = GuiFactory.createButton("Export Monthly Summary", GuiFactory.COLOR_GRAY);
        JButton logoutButton = GuiFactory.createButton("Logout", GuiFactory.COLOR_RED);

        // --- Add all buttons to the nav panel ---
        JPanel buttonGrid = new JPanel(new GridLayout(0, 1, 5, 5));
        buttonGrid.setOpaque(false);

        buttonGrid.add(GuiFactory.createSectionHeader("Transactions"));
        buttonGrid.add(addTransactionButton);
        buttonGrid.add(viewTransactionsButton);

        buttonGrid.add(GuiFactory.createSectionHeader("Manage"));
        buttonGrid.add(manageAccountsButton);
        buttonGrid.add(manageCategoriesButton);
        buttonGrid.add(setBudgetButton);

        buttonGrid.add(GuiFactory.createSectionHeader("Reports"));
        buttonGrid.add(checkBudgetButton);
        buttonGrid.add(monthlyReportButton);
        buttonGrid.add(topSpendingButton);
        buttonGrid.add(netSavingsButton);

        buttonGrid.add(GuiFactory.createSectionHeader("Tools"));
        buttonGrid.add(exportAllButton);
        buttonGrid.add(exportSummaryButton);

        buttonGrid.add(Box.createVerticalStrut(20)); // Add some space
        buttonGrid.add(logoutButton);

        navPanel.add(buttonGrid);

        navPanel.setPreferredSize(new Dimension(250, 0));

        add(new JScrollPane(navPanel), BorderLayout.WEST);

        // === 4. ACTION LISTENERS (All buttons are here) ===

        viewTransactionsButton.addActionListener(e -> {
            TransactionPanel txPanel = new TransactionPanel(financeService);
            showPanel(txPanel);
        });

        addTransactionButton.addActionListener(e -> {
            AddTransactionPanel addTxPanel = new AddTransactionPanel(financeService);
            showPanel(addTxPanel);
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

        exportAllButton.addActionListener(e -> {
            // This now calls the multithreaded version
            exportAllTransactionsWithWorker();
        });

        exportSummaryButton.addActionListener(e -> {
            ExportSummaryPanel summaryPanel = new ExportSummaryPanel(financeService, exportService);
            showPanel(summaryPanel);
        });

        logoutButton.addActionListener(e -> {
            Session.logout();
            mainFrame.switchToPanel("LOGIN");
        });
    }

    private void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Using a SwingWorker to prevent the GUI from freezing.
     */
    private void exportAllTransactionsWithWorker() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save All Transactions As...");

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String defaultFilename = String.format("transactions_%s_%s.csv",
                Session.getUser().getUsername(), timestamp);
        fileChooser.setSelectedFile(new File(defaultFilename));

        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV File (*.csv)", "csv"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {

            File selectedFile = fileChooser.getSelectedFile();

            if (!selectedFile.getAbsolutePath().endsWith(".csv")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".csv");
            }


            final File fileToSave = selectedFile;


            // 1. Show a "loading" cursor so the user knows work is happening
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            // 2. Create a new SwingWorker.
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {

                private String errorMessage = null; // To store any error

                @Override
                protected Boolean doInBackground() throws Exception {
                    // 3.NEW WORKER THREAD.
                    try {
                        int userId = Session.getUser().getUserId();
                        return exportService.exportAllTransactions(userId, fileToSave);
                    } catch (IOException ex) {
                        errorMessage = ex.getMessage(); // Save error
                        return false; // Indicate failure
                    }
                }

                @Override
                protected void done() {
                    // 4. This code runs back on the MAIN GUI THREAD (EDT).
                    setCursor(Cursor.getDefaultCursor());

                    try {
                        boolean success = get();

                        if (success) {
                            JOptionPane.showMessageDialog(DashboardPanel.this,
                                    // This also uses the 'final' variable, which is fine
                                    "Transactions exported successfully to:\n" + fileToSave.getAbsolutePath(),
                                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                        } else if (errorMessage != null) {
                            // If we caught an error, show it
                            throw new Exception(errorMessage);
                        } else {
                            // If no error, but success=false, it means no data
                            JOptionPane.showMessageDialog(DashboardPanel.this,
                                    "You have no transactions to export.",
                                    "Export Info", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (Exception ex) {
                        // Handle any exception that happened
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(DashboardPanel.this,
                                "An error occurred while saving the file:\n" + ex.getMessage(),
                                "Export Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

            // 6. Start the worker thread!
            worker.execute();
        }
    }
}