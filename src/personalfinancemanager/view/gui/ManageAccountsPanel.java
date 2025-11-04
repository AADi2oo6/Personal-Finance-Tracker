package personalfinancemanager.view.gui;

import java.awt.*;
import javax.swing.*;
import java.math.BigDecimal;
import java.util.List;
import personalfinancemanager.auth.Session;
import personalfinancemanager.models.Account;
import personalfinancemanager.service.FinanceService;

public class ManageAccountsPanel extends JPanel {

    private final FinanceService financeService;
    private final DefaultListModel<Account> listModel;
    private final JList<Account> accountList;
    private final JTextField nameField;
    private final JTextField balanceField;

    public ManageAccountsPanel(FinanceService financeService) {
        this.financeService = financeService;
        setLayout(new BorderLayout(10, 10)); // Gaps between components
        setBorder(BorderFactory.createTitledBorder("Manage Accounts"));

        // --- 1. Form for adding a new account (at the top) ---
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanel.add(new JLabel("Account Name:"));
        nameField = new JTextField(15);
        addPanel.add(nameField);

        addPanel.add(new JLabel("Opening Balance:"));
        balanceField = new JTextField(10);
        addPanel.add(balanceField);

        JButton addButton = new JButton("Create Account");
        addPanel.add(addButton);

        add(addPanel, BorderLayout.NORTH);

        // --- 2. List of existing accounts (in the center) ---
        listModel = new DefaultListModel<>();
        accountList = new JList<>(listModel);
        accountList.setFont(new Font("Arial", Font.PLAIN, 14));
        // Use our nice toString() method from Account.java
        // We'll also add a custom renderer to show balance
        accountList.setCellRenderer(new AccountListRenderer());

        add(new JScrollPane(accountList), BorderLayout.CENTER);

        // --- 3. NEW: Panel for deletion (at the bottom) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deleteButton = new JButton("Delete Selected Account");
        bottomPanel.add(deleteButton);
        add(bottomPanel, BorderLayout.SOUTH);


        // --- 4. Load initial data ---
        loadAccounts();

        // --- 5. Action Listeners ---
        addButton.addActionListener(e -> createAccount());

        // NEW: Action listener for the delete button
        deleteButton.addActionListener(e -> deleteAccount());
    }

    private void loadAccounts() {
        try {
            int userId = Session.getUser().getUserId();
            // This uses the method from your FinanceService.java
            List<Account> accounts = financeService.getAccountsByUser(userId);

            listModel.clear();
            for (Account acc : accounts) {
                listModel.addElement(acc);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading accounts: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createAccount() {
        String name = nameField.getText();
        String balanceText = balanceField.getText();

        if (name.isBlank()) {
            JOptionPane.showMessageDialog(this, "Account name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double balance;
        try {
            // Use BigDecimal for safe parsing, then convert to double
            balance = new BigDecimal(balanceText).doubleValue();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid balance. Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int userId = Session.getUser().getUserId();
            // This uses the method from your FinanceService.java
            boolean success = financeService.createAccount(userId, name, balance);

            if (success) {
                JOptionPane.showMessageDialog(this, "Account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Clear the form and reload the list
                nameField.setText("");
                balanceField.setText("");
                loadAccounts();
            } else {
                throw new Exception("Save operation returned false.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating account: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * NEW: Method to delete the selected account.
     */
    private void deleteAccount() {
        // 1. Get the selected account from the list
        Account selectedAccount = accountList.getSelectedValue();

        // 2. Check if anything is selected
        if (selectedAccount == null) {
            JOptionPane.showMessageDialog(this, "Please select an account from the list to delete.", "No Account Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Confirm with the user (very important for deletion)
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the account '" + selectedAccount.getName() + "'?\nThis action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            try {
                // 4. Call the delete method from your FinanceService
                boolean success = financeService.deleteAccount(selectedAccount.getAccountId());

                if (success) {
                    JOptionPane.showMessageDialog(this, "Account deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // 5. Refresh the list
                    loadAccounts();
                } else {
                    // This might fail if the account has transactions (foreign key constraint)
                    throw new Exception("Delete operation returned false.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting account: " + e.getMessage() + "\n(Note: You cannot delete an account that has transactions.)", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Custom renderer to make the JList look better, showing name and balance.
     */
    private static class AccountListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            // Start with the default look
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Account account) {
                // Use HTML to format the text in two parts
                String text = String.format(
                        "<html><body style='width: 200px;'><b> %s     -></b>" +
                                "<span style='float: right; color: gray;'>     ₹%.2f</span>" +
                                "</body></html>",
                        account.getName(),
                        account.getBalance()
                );
                setText(text);
                setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding
            }
            return c;
        }
    }
}

