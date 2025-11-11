package personalfinancemanager.view.gui;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import personalfinancemanager.auth.Session;
import personalfinancemanager.models.Account;
import personalfinancemanager.models.Category;
import personalfinancemanager.models.Transaction;
import personalfinancemanager.models.Income;
import personalfinancemanager.models.Expense;
import personalfinancemanager.service.FinanceService;

/**
 * Modern version of AddTransactionPanel with enhanced GUI styling.
 * Features: Card-based design, modern colors, better validation, improved UX.
 */
public class AddTransactionPanel_Modern extends JPanel {

    private final FinanceService financeService;
    private final JComboBox<Account> accountComboBox;
    private final JComboBox<Category> categoryComboBox;
    private final JComboBox<String> typeComboBox;
    private final JTextField amountField;
    private final JTextField noteField;

    public AddTransactionPanel_Modern(FinanceService financeService) {
        this.financeService = financeService;

        // Modern styling
        setBackground(GuiFactory.COLOR_LIGHT);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = GuiFactory.INSETS_LARGE;

        // Create a centered white card panel
        JPanel formPanel = GuiFactory.createCardPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setPreferredSize(new Dimension(500, 450));

        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = GuiFactory.INSETS_MEDIUM;
        formGbc.fill = GridBagConstraints.HORIZONTAL;

        // Header
        JLabel headerLabel = GuiFactory.createPanelHeader("Add New Transaction");
        formGbc.gridx = 0;
        formGbc.gridy = 0;
        formGbc.gridwidth = 2;
        formPanel.add(headerLabel, formGbc);

        // Reset for content
        formGbc.gridwidth = 1;
        formGbc.gridy++;

        // --- Row 1: Type (Income/Expense) ---
        JLabel typeLabel = GuiFactory.createLabel("Transaction Type:");
        formGbc.gridx = 0;
        formPanel.add(typeLabel, formGbc);

        typeComboBox = new JComboBox<>(new String[]{"EXPENSE", "INCOME"});
        typeComboBox.setFont(GuiFactory.FONT_TEXT);
        formGbc.gridx = 1;
        formPanel.add(typeComboBox, formGbc);

        formGbc.gridy++;

        // --- Row 2: Account ---
        JLabel accountLabel = GuiFactory.createLabel("Account:");
        formGbc.gridx = 0;
        formPanel.add(accountLabel, formGbc);

        accountComboBox = new JComboBox<>();
        accountComboBox.setFont(GuiFactory.FONT_TEXT);
        formGbc.gridx = 1;
        formPanel.add(accountComboBox, formGbc);

        formGbc.gridy++;

        // --- Row 3: Category ---
        JLabel categoryLabel = GuiFactory.createLabel("Category:");
        formGbc.gridx = 0;
        formPanel.add(categoryLabel, formGbc);

        categoryComboBox = new JComboBox<>();
        categoryComboBox.setFont(GuiFactory.FONT_TEXT);
        formGbc.gridx = 1;
        formPanel.add(categoryComboBox, formGbc);

        formGbc.gridy++;

        // --- Row 4: Amount ---
        JLabel amountLabel = GuiFactory.createLabel("Amount:");
        formGbc.gridx = 0;
        formPanel.add(amountLabel, formGbc);

        amountField = GuiFactory.createTextField(20);
        formGbc.gridx = 1;
        formPanel.add(amountField, formGbc);

        formGbc.gridy++;

        // --- Row 5: Note ---
        JLabel noteLabel = GuiFactory.createLabel("Note (Optional):");
        formGbc.gridx = 0;
        formPanel.add(noteLabel, formGbc);

        noteField = GuiFactory.createTextField(20);
        formGbc.gridx = 1;
        formPanel.add(noteField, formGbc);

        formGbc.gridy++;
        formGbc.gridwidth = 2;
        formGbc.fill = GridBagConstraints.NONE;
        formGbc.anchor = GridBagConstraints.CENTER;
        formGbc.insets = new Insets(20, 0, 0, 0);

        // --- Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        JButton saveButton = GuiFactory.createButton("Save Transaction", GuiFactory.COLOR_SUCCESS);
        saveButton.setPreferredSize(new Dimension(150, 40));
        buttonPanel.add(saveButton);

        JButton resetButton = GuiFactory.createButton("Reset", GuiFactory.COLOR_GRAY);
        resetButton.setPreferredSize(new Dimension(120, 40));
        buttonPanel.add(resetButton);

        formPanel.add(buttonPanel, formGbc);

        // Add form to main panel with padding
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(formPanel, gbc);

        // --- Load data into combo boxes ---
        loadAccounts();
        loadCategories();

        // --- Action Listeners ---
        saveButton.addActionListener(e -> saveTransaction());
        resetButton.addActionListener(e -> resetForm());
    }

    private void loadAccounts() {
        try {
            int userId = Session.getUser().getUserId();
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
            List<Category> categories = financeService.getCategoriesByUser(userId);
            categoryComboBox.removeAllItems();
            for (Category cat : categories) {
                categoryComboBox.addItem(cat);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        amountField.setText("");
        noteField.setText("");
        typeComboBox.setSelectedIndex(0);
        if (accountComboBox.getItemCount() > 0) {
            accountComboBox.setSelectedIndex(0);
        }
        if (categoryComboBox.getItemCount() > 0) {
            categoryComboBox.setSelectedIndex(0);
        }
        amountField.requestFocus();
    }

    private void saveTransaction() {
        try {
            // 1. Get selected items
            Account selectedAccount = (Account) accountComboBox.getSelectedItem();
            Category selectedCategory = (Category) categoryComboBox.getSelectedItem();
            String type = (String) typeComboBox.getSelectedItem();

            if (selectedAccount == null || selectedCategory == null) {
                JOptionPane.showMessageDialog(this, "Please select an account and category.", 
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Validate Amount
            BigDecimal amountDecimal;
            try {
                String amountText = amountField.getText().trim();
                if (amountText.isEmpty()) {
                    throw new NumberFormatException("Amount cannot be empty.");
                }
                amountDecimal = new BigDecimal(amountText);
                if (amountDecimal.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new NumberFormatException("Amount must be positive.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid amount. Please enter a positive number.", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double amount = amountDecimal.doubleValue();

            // 3. Get other fields
            String note = noteField.getText().trim();
            LocalDateTime timestamp = LocalDateTime.now();
            LocalDateTime createdAt = LocalDateTime.now();

            // 4. Create Transaction object
            Transaction tx;
            if ("INCOME".equals(type)) {
                tx = new Income(0, selectedAccount.getAccountId(), selectedCategory.getCategoryId(), 
                    amount, timestamp, note, createdAt);
            } else {
                tx = new Expense(0, selectedAccount.getAccountId(), selectedCategory.getCategoryId(), 
                    amount, timestamp, note, createdAt);
            }

            // 5. Save via service
            if (financeService.addTransaction(tx)) {
                JOptionPane.showMessageDialog(this, 
                    "✓ Transaction saved successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                resetForm();
                loadAccounts();
                loadCategories();
            } else {
                throw new Exception("Save operation returned false.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving transaction: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
