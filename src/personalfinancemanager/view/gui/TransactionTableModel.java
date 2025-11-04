package personalfinancemanager.view.gui;

import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import personalfinancemanager.models.Transaction;

public class TransactionTableModel extends AbstractTableModel {

    // Make this modifiable
    private List<Transaction> transactions;
    private final String[] columnNames = {"ID", "Type", "Amount", "Timestamp", "Note"};
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public TransactionTableModel(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public int getRowCount() {
        return transactions.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Transaction tx = transactions.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return tx.getTransactionId();
            case 1:
                return tx.getType();
            case 2:
                return tx.getAmount(); // Return the raw Double for proper sorting
            case 3:
                return tx.getTimestamp().format(formatter);
            case 4:
                return tx.getNote();
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        // Be more specific for better renderer and sorter behavior
        switch (columnIndex) {
            case 0: // ID
                return Integer.class;
            case 2: // Amount
                return Double.class;
            default:
                return String.class;
        }
    }

    // --- NEW METHOD 1 ---
    /**
     * Helper method to get the Transaction object from a specific row.
     * @param modelRow The row index in the model (not the view)
     * @return The Transaction object.
     */
    public Transaction getTransactionAt(int modelRow) {
        return transactions.get(modelRow);
    }

    // --- NEW METHOD 2 ---
    /**
     * Updates the table with a new list of transactions and refreshes the view.
     * @param newTransactions The new list of transactions.
     */
    public void setTransactions(List<Transaction> newTransactions) {
        this.transactions = newTransactions;
        // This tells the JTable to refresh its data
        fireTableDataChanged();
    }
}
