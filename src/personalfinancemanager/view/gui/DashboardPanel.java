package personalfinancemanager.view.gui;

import java.awt.*;
import javax.swing.*;
import personalfinancemanager.auth.Session;
import personalfinancemanager.service.FinanceService;

public class DashboardPanel extends JPanel {

    private final MainFrame mainFrame;
    private final FinanceService financeService;
    private final JPanel contentPanel; // Make contentPanel a field

    public DashboardPanel(MainFrame mainFrame, FinanceService financeService) {
        this.mainFrame = mainFrame;
        this.financeService = financeService;

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
            // You will need to create this TransactionPanel.java file
            TransactionPanel txPanel = new TransactionPanel(financeService);
            showPanel(txPanel);
        });

        // **** THIS IS THE FIX ****
        // Changed this from a JOptionPane to open your new panel
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

        // FIXED: Added listener
        manageAccountsButton.addActionListener(e -> {
            // This creates and shows your new panel
            ManageAccountsPanel accountsPanel = new ManageAccountsPanel(financeService);
            showPanel(accountsPanel);
        });

        // FIXED: Added listener
        manageCategoriesButton.addActionListener(e -> {
            // This creates and shows your new panel
            ManageCategoriesPanel catPanel = new ManageCategoriesPanel(financeService);
            showPanel(catPanel);
        });

        // --- Add listeners for all other new buttons ---

        setBudgetButton.addActionListener(e -> {
            // This creates and shows your new panel
            SetBudgetPanel budgetPanel = new SetBudgetPanel(financeService);
            showPanel(budgetPanel);
        });

        checkBudgetButton.addActionListener(e -> {
            CheckBudgetPanel checkBudget = new CheckBudgetPanel(financeService);
            showPanel(checkBudget);
        });

        monthlyReportButton.addActionListener(e -> {
            MonthlyReportPanel monthlyReport = new MonthlyReportPanel(financeService);
            showPanel(monthlyReport);
        });

        topSpendingButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Top Spending panel not yet implemented.");
        });

        netSavingsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Net Savings panel not yet implemented.");
        });

        exportAllButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Export All feature not yet implemented.");
        });

        exportSummaryButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Export Summary feature not yet implemented.");
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
}
