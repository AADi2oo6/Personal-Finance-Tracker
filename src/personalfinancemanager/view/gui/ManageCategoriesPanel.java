package personalfinancemanager.view.gui;

import java.awt.*;
import javax.swing.*;
import java.util.List;
import personalfinancemanager.auth.Session;
import personalfinancemanager.models.Category;
import personalfinancemanager.service.FinanceService;

public class ManageCategoriesPanel extends JPanel {

    private final FinanceService financeService;
    private final DefaultListModel<Category> listModel;
    private final JList<Category> categoryList;
    private final JTextField nameField;

    public ManageCategoriesPanel(FinanceService financeService) {
        this.financeService = financeService;
        setLayout(new BorderLayout(10, 10)); // Gaps
        setBorder(BorderFactory.createTitledBorder("Manage Categories"));

        // --- 1. Form for adding a new category (at the top) ---
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanel.add(new JLabel("Category Name:"));
        nameField = new JTextField(20);
        addPanel.add(nameField);

        JButton addButton = new JButton("Create Category");
        addPanel.add(addButton);

        add(addPanel, BorderLayout.NORTH);

        // --- 2. List of existing categories (in the center) ---
        listModel = new DefaultListModel<>();
        categoryList = new JList<>(listModel);
        categoryList.setFont(new Font("Arial", Font.PLAIN, 14));
        // We'll add padding to list items
        categoryList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Category category) {
                    // Use the toString() method from Category.java
                    setText(category.toString());
                    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                }
                return c;
            }
        });

        add(new JScrollPane(categoryList), BorderLayout.CENTER);

        // --- 3. Panel for deletion (at the bottom) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deleteButton = new JButton("Delete Selected Category");
        bottomPanel.add(deleteButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- 4. Load initial data ---
        loadCategories();

        // --- 5. Action Listeners ---
        addButton.addActionListener(e -> createCategory());
        deleteButton.addActionListener(e -> deleteCategory());
    }

    private void loadCategories() {
        try {
            int userId = Session.getUser().getUserId();
            // This uses the method from your FinanceService.java
            List<Category> categories = financeService.getCategoriesByUser(userId);

            listModel.clear();
            for (Category cat : categories) {
                listModel.addElement(cat);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createCategory() {
        String name = nameField.getText();

        if (name.isBlank()) {
            JOptionPane.showMessageDialog(this, "Category name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int userId = Session.getUser().getUserId();
            // This uses the method from your FinanceService.java
            boolean success = financeService.createCategory(userId, name);

            if (success) {
                JOptionPane.showMessageDialog(this, "Category created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Clear the form and reload the list
                nameField.setText("");
                loadCategories();
            } else {
                throw new Exception("Save operation returned false.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating category: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCategory() {
        // 1. Get the selected category
        Category selectedCategory = categoryList.getSelectedValue();

        // 2. Check if anything is selected
        if (selectedCategory == null) {
            JOptionPane.showMessageDialog(this, "Please select a category from the list to delete.", "No Category Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Confirm with the user
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the category '" + selectedCategory.getName() + "'?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            try {
                // 4. Call the delete method from FinanceService
                boolean success = financeService.deleteCategory(selectedCategory.getCategoryId());

                if (success) {
                    JOptionPane.showMessageDialog(this, "Category deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // 5. Refresh the list
                    loadCategories();
                } else {
                    throw new Exception("Delete operation returned false.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting category: " + e.getMessage() + "\n(Note: You cannot delete a category that is used by transactions.)", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
