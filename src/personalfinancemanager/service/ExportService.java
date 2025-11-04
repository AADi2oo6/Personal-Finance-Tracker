package personalfinancemanager.service;

import personalfinancemanager.models.Transaction;

// Import File and File classes
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
// End of new imports
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors; // Added this import

public class ExportService {
    private final FinanceService financeService;

    public ExportService(FinanceService financeService) {
        this.financeService = financeService;
    }

    /**
     * Original console method. We'll keep it for compatibility.
     * It now calls our new, more flexible method.
     */
    public boolean exportAllTransactions(int userId) {
        // Create the 'exports' directory if it doesn't exist
        new File("exports").mkdirs();

        String fileName = generateFilename("transactions");
        File file = new File(fileName);

        // We must wrap the call in a try-catch block
        try {
            return exportAllTransactions(userId, file);
        } catch (IOException e) {
            // This matches the error handling in your other methods
            System.err.println("Export failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * NEW OVERLOADED METHOD for the GUI.
     * This one writes to a specific File object provided by JFileChooser.
     *
     * @param userId The user's ID
     * @param file   The File object to write to.
     * @return true on success, false if no transactions.
     * @throws IOException if the file writing fails.
     */
    public boolean exportAllTransactions(int userId, File file) throws IOException {
        List<Transaction> txList = financeService.getAllTransactions(userId);
        if (txList.isEmpty()) return false;

        try (FileWriter writer = new FileWriter(file)) {
            writer.write("TransactionID,Type,Amount,Category,Account,Timestamp,Note\n");

            // We need to get Account and Category names for a user-friendly export
            Map<Integer, String> accountMap = financeService.getAccountsByUser(userId)
                    .stream().collect(Collectors.toMap(
                            personalfinancemanager.models.Account::getAccountId,
                            personalfinancemanager.models.Account::getName
                    ));

            Map<Integer, String> categoryMap = financeService.getCategoriesByUser(userId)
                    .stream().collect(Collectors.toMap(
                            personalfinancemanager.models.Category::getCategoryId,
                            personalfinancemanager.models.Category::getName
                    ));

            for (Transaction tx : txList) {
                // Get names, with a fallback if not found
                String categoryName = categoryMap.getOrDefault(tx.getCategoryId(), "N/A");
                String accountName = accountMap.getOrDefault(tx.getAccountId(), "N/A");

                // Escape commas in notes to prevent breaking CSV
                String note = (tx.getNote() != null ? tx.getNote() : "").replace(",", ";");

                writer.write(String.format("%d,%s,%.2f,%s,%s,%s,%s\n",
                        tx.getTransactionId(),
                        tx.getType(),
                        tx.getAmount(),
                        categoryName,
                        accountName,
                        tx.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        note));
            }
            return true;
        } catch (IOException e) {
            System.err.println("Export failed: " + e.getMessage());
            // Re-throw the exception so the GUI can catch it
            throw e;
        }
    }

    /**
     * Original console method.
     * NOW UPDATED to call the new GUI-capable method.
     */
    public boolean exportMonthlySummary(int userId, int year, int month) {
        new File("exports").mkdirs();
        String fileName = generateFilename("summary_" + year + "_" + month);
        File file = new File(fileName);

        try {
            // Call the new, overloaded method
            return exportMonthlySummary(userId, year, month, file);
        } catch (IOException e) {
            System.err.println("Export failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * NEW OVERLOADED METHOD for the GUI.
     * This one writes to a specific File object provided by JFileChooser.
     *
     * @param userId The user's ID
     * @param year   The year
     * @param month  The month
     * @param file   The File object to write to.
     * @return true on success, false if no data.
     * @throws IOException if the file writing fails.
     */
    public boolean exportMonthlySummary(int userId, int year, int month, File file) throws IOException {
        Map<String, Double> breakdown = financeService.getMonthlyCategoryBreakdown(userId, year, month);
        Double budget = financeService.getMonthlyBudget(userId, year, month);

        if (breakdown.isEmpty()) return false;

        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Category,Amount\n");
            for (var entry : breakdown.entrySet()) {
                writer.write(entry.getKey() + "," + String.format("%.2f", entry.getValue()) + "\n");
            }

            // Add budget summary if it exists
            if (budget != null) {
                writer.write("\nTotal Budget," + budget + "\n");
                double spent = breakdown.values().stream().mapToDouble(Double::doubleValue).sum();
                writer.write("Total Spent," + spent + "\n");
                writer.write("Remaining," + (budget - spent) + "\n");
            }
            return true;
        } catch (IOException e) {
            System.err.println("Export failed: " + e.getMessage());
            // Re-throw for the GUI to catch
            throw e;
        }
    }

    private String generateFilename(String prefix) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return "exports/" + prefix + "_" + timestamp + ".csv";
    }
}

