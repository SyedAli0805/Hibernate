package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class OrderPage extends JPanel {
    private Map<String, Table[]> diningOptions;
    private JPanel tablesPanel;
    private JPanel categoriesAndProductsPanel;
    private JPanel orderSummaryPanel;
    private Map<String, Category> categories;
    private Table currentTable;
    private JButton confirmOrderButton;
    private JButton paidButton;
    private JButton cancelOrderButton;
    private JLabel totalAmountLabel;
    private String currentOption;

    public OrderPage(Dashboard dashboard) {
        setLayout(new BorderLayout());

        // Initialize dining options with 25 tables each
        diningOptions = new HashMap<>();
        String[] options = {"Dine In", "Family Hall", "Take Away", "Home Delivery", "Car Parking"};
        for (String option : options) {
            Table[] tables = new Table[25];
            for (int i = 0; i < 25; i++) {
                tables[i] = new Table(i + 1);
            }
            diningOptions.put(option, tables);
        }

        // Set default option to "Dine In"
        currentOption = "Dine In";

        // Create a panel for order types and tables
        JPanel orderTypeAndTablesPanel = new JPanel(new BorderLayout());
        JPanel orderTypePanel = new JPanel(new GridLayout(1, 5));
        for (String option : options) {
            JButton optionButton = new JButton(option);
            optionButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    currentOption = option;
                    showTables(option);
                }
            });
            orderTypePanel.add(optionButton);
        }
        orderTypeAndTablesPanel.add(orderTypePanel, BorderLayout.NORTH);

        // Create a panel to display tables
        tablesPanel = new JPanel(new GridLayout(5, 5));
        orderTypeAndTablesPanel.add(tablesPanel, BorderLayout.CENTER);
        add(orderTypeAndTablesPanel, BorderLayout.EAST);

        // Create a panel for categories and products
        categoriesAndProductsPanel = new JPanel(new BorderLayout());
        add(categoriesAndProductsPanel, BorderLayout.CENTER);

        // Create a panel for order summary (receipt)
        orderSummaryPanel = new JPanel(new BorderLayout());
        add(orderSummaryPanel, BorderLayout.SOUTH);

        // Initialize categories and add products
        initializeCategories();

        // Show tables for the default option
        showTables(currentOption);

        // Create order buttons
        confirmOrderButton = new JButton("Confirm Order");
        confirmOrderButton.setEnabled(false);
        confirmOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                confirmOrder();
            }
        });

        paidButton = new JButton("Paid");
        paidButton.setEnabled(false);
        paidButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                payAndResetTable();
            }
        });

        cancelOrderButton = new JButton("Cancel Order");
        cancelOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelOrder();
            }
        });

        totalAmountLabel = new JLabel("Total: PKR 0");
        JPanel orderActionsPanel = new JPanel(new GridLayout(4, 1));
        orderActionsPanel.add(totalAmountLabel);
        orderActionsPanel.add(confirmOrderButton);
        orderActionsPanel.add(paidButton);
        orderActionsPanel.add(cancelOrderButton);

        orderSummaryPanel.add(orderActionsPanel, BorderLayout.SOUTH);

        // Reset products and categories view
        showCategories();
    }

    private void initializeCategories() {

    }

    private void showCategories() {
        categoriesAndProductsPanel.removeAll();

        JLabel categoriesLabel = new JLabel("Categories");
        categoriesAndProductsPanel.add(categoriesLabel, BorderLayout.NORTH);

        JPanel categoriesPanel = new JPanel(new GridLayout(0, 2));
        for (Category category : categories.values()) {
            JButton categoryButton = new JButton(category.getName());
            categoryButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showProducts(category);
                }
            });
            categoriesPanel.add(categoryButton);
        }

        categoriesAndProductsPanel.add(categoriesPanel, BorderLayout.CENTER);
        categoriesAndProductsPanel.revalidate();
        categoriesAndProductsPanel.repaint();
    }

    private void showProducts(Category category) {
        categoriesAndProductsPanel.removeAll();

        JLabel productsLabel = new JLabel("Products - " + category.getName());
        categoriesAndProductsPanel.add(productsLabel, BorderLayout.NORTH);

        JPanel productsPanel = new JPanel(new GridLayout(0, 2));
        for (String product : category.getProducts()) {
            JPanel productPanel = new JPanel(new BorderLayout());
            JCheckBox productCheckBox = new JCheckBox(product);
            JTextField quantityField = new JTextField("1", 2);

            // If the product is already selected, check the checkbox and update quantity
            if (currentTable.getSelectedProducts().containsKey(product)) {
                productCheckBox.setSelected(true);
                quantityField.setText(currentTable.getSelectedProducts().get(product).toString());
            }

            productCheckBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (productCheckBox.isSelected()) {
                        int quantity = Integer.parseInt(quantityField.getText());
                        currentTable.getSelectedProducts().put(product, quantity);
                    } else {
                        currentTable.getSelectedProducts().remove(product);
                    }
                    updateOrderSummary();
                }
            });

            quantityField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (productCheckBox.isSelected()) {
                        int quantity = Integer.parseInt(quantityField.getText());
                        currentTable.getSelectedProducts().put(product, quantity);
                        updateOrderSummary();
                    }
                }
            });

            productPanel.add(productCheckBox, BorderLayout.WEST);
            productPanel.add(quantityField, BorderLayout.EAST);
            productsPanel.add(productPanel);
        }

        categoriesAndProductsPanel.add(productsPanel, BorderLayout.CENTER);

        JButton backToCategoriesButton = new JButton("Back to Categories");
        backToCategoriesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showCategories();
            }
        });
        categoriesAndProductsPanel.add(backToCategoriesButton, BorderLayout.SOUTH);

        categoriesAndProductsPanel.revalidate();
        categoriesAndProductsPanel.repaint();
    }

    private void updateOrderSummary() {
        orderSummaryPanel.removeAll();

        if (currentTable != null) {
            JPanel productsListPanel = new JPanel(new GridLayout(0, 1));
            for (Map.Entry<String, Integer> entry : currentTable.getSelectedProducts().entrySet()) {
                String product = entry.getKey();
                int quantity = entry.getValue();
                productsListPanel.add(new JLabel(product + " x " + quantity));
            }
            orderSummaryPanel.add(productsListPanel, BorderLayout.CENTER);

            // Update total amount with quantities
            int totalAmount = 0;
            for (int quantity : currentTable.getSelectedProducts().values()) {
                totalAmount += quantity * 500; // Assuming each product costs PKR 500
            }
            totalAmountLabel.setText("Total: PKR " + totalAmount);

            confirmOrderButton.setEnabled(!currentTable.getSelectedProducts().isEmpty());
        }

        orderSummaryPanel.add(confirmOrderButton, BorderLayout.SOUTH);

        orderSummaryPanel.revalidate();
        orderSummaryPanel.repaint();
    }

    private void confirmOrder() {
        if (currentTable != null) {
            currentTable.placeOrder();
            showReceiptDialog("Order Slip", currentTable.getSelectedProducts());
            showReceiptDialog("Kitchen Slip", currentTable.getSelectedProducts());
            paidButton.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a table first!");
        }
    }

    private void payAndResetTable() {
        if (currentTable != null) {
            showReceiptDialog("Final Receipt", currentTable.getSelectedProducts());
            currentTable.markAsPaid();
            resetOrder();
        }
    }

    private void cancelOrder() {
        if (currentTable != null) {
            currentTable.resetTable();
            resetOrder();
        }
    }

    private void showTables(String option) {
        tablesPanel.removeAll();

        Table[] tables = diningOptions.get(option);
        if (tables == null) {
            System.out.println("Error: No tables found for the option: " + option);
            return;
        }
        for (Table table : tables) {
            JButton tableButton = table.getButton();
            tableButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    currentTable = table;
                    highlightSelectedTable();
                    showCategories();
                }
            });
            tablesPanel.add(tableButton);
        }

        tablesPanel.revalidate();
        tablesPanel.repaint();
    }

    private void highlightSelectedTable() {
        Table[] tables = diningOptions.get(currentOption);

        if (tables == null) {
            System.out.println("Error: Tables for option '" + currentOption + "' are null.");
            return;
        }

        for (Table table : tables) {
            JButton button = table.getButton();
            if (table == currentTable) {
                button.setBackground(Color.BLUE); // Highlight selected table
            } else {
                button.setBackground(table.getColor());
            }
        }
    }

    private void showReceiptDialog(String title, Map<String, Integer> items) {
        StringBuilder receipt = new StringBuilder();
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            receipt.append(entry.getKey()).append(" x ").append(entry.getValue()).append("\n");
        }

        JOptionPane.showMessageDialog(this, receipt.toString(), title, JOptionPane.INFORMATION_MESSAGE);
    }


    private void resetOrder() {
        currentTable = null;
        paidButton.setEnabled(false);
        confirmOrderButton.setEnabled(false);
        showCategories();
        updateOrderSummary();
    }
}
