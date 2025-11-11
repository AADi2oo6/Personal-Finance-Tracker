package personalfinancemanager.view.gui;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
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

        // Modern background
        setBackground(GuiFactory.COLOR_LIGHT);
        setLayout(new BorderLayout());

        // === 1. WELCOME HEADER ===
        String username = Session.getUser() != null ? Session.getUser().getUsername() : "User";
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(GuiFactory.COLOR_PRIMARY_DARK);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel welcomeLabel = new JLabel("Welcome, " + username + " 👋");
        welcomeLabel.setFont(GuiFactory.FONT_TITLE);
        welcomeLabel.setForeground(Color.WHITE);

        JLabel dateLabel = new JLabel(java.time.LocalDate.now().toString());
        dateLabel.setFont(GuiFactory.FONT_TEXT);
        dateLabel.setForeground(GuiFactory.COLOR_LIGHT);

        JPanel headerTextPanel = new JPanel();
        headerTextPanel.setLayout(new BoxLayout(headerTextPanel, BoxLayout.Y_AXIS));
        headerTextPanel.setBackground(GuiFactory.COLOR_PRIMARY_DARK);
        headerTextPanel.add(welcomeLabel);
        headerTextPanel.add(Box.createVerticalStrut(5));
        headerTextPanel.add(dateLabel);

        headerPanel.add(headerTextPanel, BorderLayout.WEST);

        // Add logout button to header
        JButton logoutButton = GuiFactory.createButton("Logout", GuiFactory.COLOR_DANGER);
        logoutButton.setPreferredSize(new Dimension(100, 35));
        headerPanel.add(logoutButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // === 2. MAIN CONTENT AREA ===
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Add an initial "getting started" panel
        JPanel gettingStartedPanel = new JPanel(new GridBagLayout());
        gettingStartedPanel.setBackground(Color.WHITE);
        JLabel gettingStartedLabel = new JLabel(
                "<html><center><h2>Welcome to Personal Finance Tracker</h2>" +
                        "<br>Select an option from the menu to get started.<br>" +
                        "<br>You can add transactions, manage accounts, view reports and much more!</center></html>"
        );
        gettingStartedLabel.setFont(GuiFactory.FONT_LABEL);
        gettingStartedLabel.setForeground(GuiFactory.COLOR_GRAY);
        gettingStartedPanel.add(gettingStartedLabel);
        contentPanel.add(gettingStartedPanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // === 3. MODERN SIDEBAR NAVIGATION ===
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(GuiFactory.COLOR_DARK);
        navPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Create organized button groups
        JPanel transactionSection = createSectionPanel("Transactions", GuiFactory.COLOR_SUCCESS);
        JButton addTransactionButton = GuiFactory.createButton("+ Add Transaction", GuiFactory.COLOR_SUCCESS);
        JButton viewTransactionsButton = GuiFactory.createButton("View / Edit / Delete", GuiFactory.COLOR_ACCENT);
        transactionSection.add(addTransactionButton);
        transactionSection.add(Box.createVerticalStrut(8));
        transactionSection.add(viewTransactionsButton);

        JPanel manageSection = createSectionPanel("Manage", GuiFactory.COLOR_BLUE);
        JButton manageAccountsButton = GuiFactory.createButton("Manage Accounts", GuiFactory.COLOR_BLUE);
        JButton manageCategoriesButton = GuiFactory.createButton("Manage Categories", GuiFactory.COLOR_ACCENT);
        JButton setBudgetButton = GuiFactory.createButton("Set Monthly Budget", GuiFactory.COLOR_ORANGE);
        manageSection.add(manageAccountsButton);
        manageSection.add(Box.createVerticalStrut(8));
        manageSection.add(manageCategoriesButton);
        manageSection.add(Box.createVerticalStrut(8));
        manageSection.add(setBudgetButton);

        JPanel reportsSection = createSectionPanel("Reports", GuiFactory.COLOR_PURPLE);
        JButton checkBudgetButton = GuiFactory.createButton("Check Budget Status", GuiFactory.COLOR_PURPLE);
        JButton monthlyReportButton = GuiFactory.createButton("Monthly Category Report", GuiFactory.COLOR_ACCENT);
        JButton topSpendingButton = GuiFactory.createButton("Top Spending Report", GuiFactory.COLOR_ACCENT);
        JButton netSavingsButton = GuiFactory.createButton("View Net Savings", GuiFactory.COLOR_ACCENT);
        reportsSection.add(checkBudgetButton);
        reportsSection.add(Box.createVerticalStrut(8));
        reportsSection.add(monthlyReportButton);
        reportsSection.add(Box.createVerticalStrut(8));
        reportsSection.add(topSpendingButton);
        reportsSection.add(Box.createVerticalStrut(8));
        reportsSection.add(netSavingsButton);

        JPanel toolsSection = createSectionPanel("Tools", GuiFactory.COLOR_GRAY);
        JButton exportAllButton = GuiFactory.createButton("Export All Transactions", GuiFactory.COLOR_GRAY);
        JButton exportSummaryButton = GuiFactory.createButton("Export Monthly Summary", GuiFactory.COLOR_ACCENT);
        toolsSection.add(exportAllButton);
        toolsSection.add(Box.createVerticalStrut(8));
        toolsSection.add(exportSummaryButton);

        // Add all sections to nav panel
        navPanel.add(transactionSection);
        navPanel.add(Box.createVerticalStrut(15));
        navPanel.add(manageSection);
        navPanel.add(Box.createVerticalStrut(15));
        navPanel.add(reportsSection);
        navPanel.add(Box.createVerticalStrut(15));
        navPanel.add(toolsSection);
        navPanel.add(Box.createVerticalGlue());

        JScrollPane navScrollPane = new JScrollPane(navPanel);
        navScrollPane.setPreferredSize(new Dimension(280, 0));
        navScrollPane.setBorder(null);
        navScrollPane.getViewport().setBackground(GuiFactory.COLOR_DARK);
        add(navScrollPane, BorderLayout.WEST);

        // === 4. ACTION LISTENERS ===

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

    /**
     * Create a styled section panel for organizing buttons
     */
    private JPanel createSectionPanel(String title, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(GuiFactory.COLOR_DARK);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sectionLabel = new JLabel("■ " + title);
        sectionLabel.setFont(GuiFactory.FONT_SUBHEADING);
        sectionLabel.setForeground(color);
        sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(sectionLabel);
        panel.add(Box.createVerticalStrut(10));

        return panel;
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