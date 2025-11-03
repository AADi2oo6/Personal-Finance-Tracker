package personalfinancemanager.view.gui;

import java.awt.*;
import java.math.BigDecimal; // Keep for validation
import java.time.LocalDateTime; // Use this instead of Timestamp
import java.util.List;
import javax.swing.*;
import personalfinancemanager.auth.Session;
import personalfinancemanager.models.Account;
import personalfinancemanager.models.Category;
import personalfinancemanager.models.Transaction;
import personalfinancemanager.models.Income;   // <-- Import Income
import personalfinancemanager.models.Expense;  // <-- Import Expense
import personalfinancemanager.service.FinanceService;

public class AddTransactionPanel extends JPanel {

    private final FinanceService financeService;
    private final JComboBox<Account> accountComboBox;
    private final JComboBox<Category> categoryComboBox;
    private final JComboBox<String> typeComboBox;
    private final JTextField amountField;
    private final JTextField noteField;

    public AddTransactionPanel(FinanceService financeService) {
        this.financeService = financeService;

        // Set a title for the panel
        setBorder(BorderFactory.createTitledBorder("Add New Transaction"));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // --- Row 0: Type (Income/Expense) ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Type:"), gbc);

        gbc.gridx = 1;
        // Use the exact strings from your models
        typeComboBox = new JComboBox<>(new String[]{"EXPENSE", "INCOME"});
        add(typeComboBox, gbc);

        // --- Row 1: Account ---
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Account:"), gbc);

        gbc.gridx = 1;
        accountComboBox = new JComboBox<>();
        add(accountComboBox, gbc);

        // --- Row 2: Category ---
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Category:"), gbc);

        gbc.gridx = 1;
        categoryComboBox = new JComboBox<>();
        add(categoryComboBox, gbc);

        // --- Row 3: Amount ---
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Amount:"), gbc);

        gbc.gridx = 1;
        amountField = new JTextField(15);
        add(amountField, gbc);

        // --- Row 4: Note ---
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Note:"), gbc);

        gbc.gridx = 1;
        noteField = new JTextField(15);
        add(noteField, gbc);

        // --- Row 5: Save Button ---
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2; // Span both columns
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton saveButton = new JButton("Save Transaction");
        add(saveButton, gbc);

        // --- Load data into combo boxes ---
        loadAccounts();
        loadCategories();

        // --- Action Listeners ---
        saveButton.addActionListener(e -> saveTransaction());
    }

    private void loadAccounts() {
        try {
            int userId = Session.getUser().getUserId();
            // FIX #1: Renamed method to getAccountsByUser
            List<Account> accounts = financeService.getAccountsByUser(userId);
            accountComboBox.removeAllItems();
            for (Account acc : accounts) {
                accountComboBox.addItem(acc);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading accounts: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCategories() {
        try {
            int userId = Session.getUser().getUserId();
            // FIX #2: Renamed method to getCategoriesByUser
            List<Category> categories = financeService.getCategoriesByUser(userId);
            categoryComboBox.removeAllItems();
            for (Category cat : categories) {
                categoryComboBox.addItem(cat);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveTransaction() {
        try {
            // 1. Get selected items
            Account selectedAccount = (Account) accountComboBox.getSelectedItem();
            Category selectedCategory = (Category) categoryComboBox.getSelectedItem();
            String type = (String) typeComboBox.getSelectedItem();

            // 2. Validate Amount
            BigDecimal amountDecimal; // Use BigDecimal for safe validation
            try {
                amountDecimal = new BigDecimal(amountField.getText());
                if (amountDecimal.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new NumberFormatException("Amount must be positive.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a positive number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Convert to double for your model
            double amount = amountDecimal.doubleValue();

            // 3. Get other fields
            String note = noteField.getText();
            LocalDateTime timestamp = LocalDateTime.now(); // Use LocalDateTime
            LocalDateTime createdAt = LocalDateTime.now(); // Use LocalDateTime

            // 4. Create Transaction object (THIS IS THE MAIN FIX)
            Transaction tx;
            if ("INCOME".equals(type)) {
                tx = new Income(
                        0, // 0 for new transaction, DB will auto-increment
                        selectedAccount.getAccountId(),
                        selectedCategory.getCategoryId(),
                        amount,
                        timestamp,
                        note,
                        createdAt
                );
            } else { // Default to Expense
                tx = new Expense(
                        0,
                        selectedAccount.getAccountId(),
                        selectedCategory.getCategoryId(),
                        amount,
                        timestamp,
                        note,
                        createdAt
                );
            }

            // 5. Save via service
            // FIX #3: Removed the second argument (userId)
            if (financeService.addTransaction(tx)) {
                JOptionPane.showMessageDialog(this, "Transaction saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Clear the form
                amountField.setText("");
                noteField.setText("");
                // Reload accounts to show updated balance
                loadAccounts();
                loadCategories();
            } else {
                throw new Exception("Save operation returned false.");
            }

        } catch (Exception e) {
            e.printStackTrace(); // Good for debugging
            JOptionPane.showMessageDialog(this, "Error saving transaction: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
