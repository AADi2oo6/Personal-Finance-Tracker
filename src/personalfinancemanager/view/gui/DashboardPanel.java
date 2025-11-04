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
        // We'll use a vertical BoxLayout to stack sections
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(255, 255, 255));
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Create styled buttons using our new factory ---

        // Section 1: Transactions
        // --- FIX: REMOVED navPanel.add(GuiFactory.createSectionHeader("Transactions")); ---
        JButton addTransactionButton = GuiFactory.createButton("Add Transaction", GuiFactory.COLOR_GREEN);
        JButton viewTransactionsButton = GuiFactory.createButton("View / Edit / Delete", GuiFactory.COLOR_PRIMARY);

        // Section 2: Management
        // --- FIX: REMOVED navPanel.add(GuiFactory.createSectionHeader("Manage")); ---
        JButton manageAccountsButton = GuiFactory.createButton("Manage Accounts", GuiFactory.COLOR_BLUE);
        JButton manageCategoriesButton = GuiFactory.createButton("Manage Categories", GuiFactory.COLOR_BLUE);
        JButton setBudgetButton = GuiFactory.createButton("Set Monthly Budget", GuiFactory.COLOR_ORANGE);

        // Section 3: Reports
        // --- FIX: REMOVED navPanel.add(GuiFactory.createSectionHeader("Reports")); ---
        JButton checkBudgetButton = GuiFactory.createButton("Check Budget Status", GuiFactory.COLOR_PURPLE);
        JButton monthlyReportButton = GuiFactory.createButton("Monthly Category Report", GuiFactory.COLOR_PURPLE);
        JButton topSpendingButton = GuiFactory.createButton("Top Spending Report", GuiFactory.COLOR_PURPLE);
        JButton netSavingsButton = GuiFactory.createButton("View Net Savings", GuiFactory.COLOR_PURPLE);

        // Section 4: Tools & Logout
        // --- FIX: REMOVED navPanel.add(GuiFactory.createSectionHeader("Tools")); ---
        JButton exportAllButton = GuiFactory.createButton("Export All Transactions", GuiFactory.COLOR_GRAY);
        JButton exportSummaryButton = GuiFactory.createButton("Export Monthly Summary", GuiFactory.COLOR_GRAY);
        JButton logoutButton = GuiFactory.createButton("Logout", GuiFactory.COLOR_RED);

        // --- Add all buttons to the nav panel ---
        // Use a standard layout for the buttons
        JPanel buttonGrid = new JPanel(new GridLayout(0, 1, 5, 5));
        buttonGrid.setOpaque(false);

        // --- FIX: ADDED "Transactions" header here
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

        // Set fixed size for the nav panel
        navPanel.setPreferredSize(new Dimension(250, 0));

        add(new JScrollPane(navPanel), BorderLayout.WEST);

        // === 4. ACTION LISTENERS (All buttons are here) ===

        viewTransactionsButton.addActionListener(e -> {
            TransactionPanel txPanel = new TransactionPanel(financeService);
            showPanel(txPanel);
        });

        addTransactionButton.addActionListener(e -> {
            // Updated to use the file from the context
            personalfinancemanager.view.gui.AddTransactionPanel addTxPanel = new personalfinancemanager.view.gui.AddTransactionPanel(financeService);
            showPanel(addTxPanel);
        });

        // "Edit" and "Delete" buttons are now removed.

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
            exportAllTransactions();
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
     * Handles the "Export All" action
     */
    private void exportAllTransactions() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save All Transactions As...");

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String defaultFilename = String.format("transactions_%s_%s.csv",
                Session.getUser().getUsername(), timestamp);
        fileChooser.setSelectedFile(new File(defaultFilename));

        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV File (*.csv)", "csv"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            if (!fileToSave.getAbsolutePath().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }

            try {
                int userId = Session.getUser().getUserId();
                boolean success = exportService.exportAllTransactions(userId, fileToSave);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Transactions exported successfully to:\n" + fileToSave.getAbsolutePath(),
                            "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "You have no transactions to export.",
                            "Export Info", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "An error occurred while saving the file:\n" + ex.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

