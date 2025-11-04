package personalfinancemanager.view.gui;

import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import personalfinancemanager.auth.Session;
import personalfinancemanager.models.Transaction;
import personalfinancemanager.service.FinanceService;

public class TransactionPanel extends JPanel {

    private final FinanceService financeService;
    private final JTable transactionTable;
    private final TransactionTableModel tableModel;

    public TransactionPanel(FinanceService financeService) {
        this.financeService = financeService;
        setLayout(new BorderLayout(10, 10)); // Gaps
        setBorder(BorderFactory.createTitledBorder("View, Edit, or Delete Transactions"));

        // --- 1. Table (Center) ---
        List<Transaction> transactions = financeService.getAllTransactions(Session.getUser().getUserId());
        tableModel = new TransactionTableModel(transactions);
        transactionTable = new JTable(tableModel);
        transactionTable.setAutoCreateRowSorter(true); // Enable sorting

        // Ensure only one row can be selected
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // --- Professional Formatting for Currency ---
        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Double) {
                    value = NumberFormat.getCurrencyInstance(new java.util.Locale("en", "IN")).format(value);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
        transactionTable.getColumnModel().getColumn(2).setCellRenderer(currencyRenderer); // Apply to "Amount" column
        add(new JScrollPane(transactionTable), BorderLayout.CENTER);

        // --- 2. Button Panel (South) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editButton = new JButton("Edit Selected Transaction");
        JButton deleteButton = new JButton("Delete Selected Transaction");

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- 3. Action Listeners ---
        deleteButton.addActionListener(e -> deleteSelectedTransaction());
        editButton.addActionListener(e -> editSelectedTransaction());
    }

    /**
     * Gets the selected transaction, confirms, and deletes it.
     */
    private void deleteSelectedTransaction() {
        Transaction tx = getSelectedTransaction();
        if (tx == null) return; // No transaction selected

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this " + tx.getType() + " of " +
                        NumberFormat.getCurrencyInstance(new java.util.Locale("en", "IN")).format(tx.getAmount()) + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean success = financeService.deleteTransaction(tx.getTransactionId());
                if (success) {
                    JOptionPane.showMessageDialog(this, "Transaction deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshTable(); // Refresh the table
                } else {
                    throw new Exception("Delete operation returned false.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting transaction: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Gets the selected transaction and opens dialogs to edit it.
     */
    private void editSelectedTransaction() {
        Transaction tx = getSelectedTransaction();
        if (tx == null) return; // No transaction selected

        try {
            // 1. Get new Amount
            String newAmountStr = JOptionPane.showInputDialog(
                    this,
                    "Enter new amount:",
                    tx.getAmount() // Pre-fill with old amount
            );
            if (newAmountStr == null) return; // User cancelled

            // 2. Get new Note
            String newNote = JOptionPane.showInputDialog(
                    this,
                    "Enter new note:",
                    tx.getNote() // Pre-fill with old note
            );
            if (newNote == null) newNote = ""; // Allow empty note

            // 3. Validate Amount
            double newAmount;
            try {
                newAmount = new BigDecimal(newAmountStr).doubleValue();
                if (newAmount <= 0) throw new NumberFormatException("Amount must be positive.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a positive number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 4. Call service
            boolean success = financeService.updateTransactionAmountAndNote(tx.getTransactionId(), newAmount, newNote);
            if (success) {
                JOptionPane.showMessageDialog(this, "Transaction updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable(); // Refresh the table
            } else {
                throw new Exception("Update operation returned false.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating transaction: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Helper method to get the currently selected transaction from the table.
     */
    private Transaction getSelectedTransaction() {
        int selectedViewRow = transactionTable.getSelectedRow();
        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a transaction from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        // IMPORTANT: Convert view row to model row in case of sorting
        int modelRow = transactionTable.convertRowIndexToModel(selectedViewRow);
        return tableModel.getTransactionAt(modelRow);
    }

    /**
     * Re-fetches all transactions from the database and updates the table.
     */
    private void refreshTable() {
        List<Transaction> transactions = financeService.getAllTransactions(Session.getUser().getUserId());
        tableModel.setTransactions(transactions);
    }
}
