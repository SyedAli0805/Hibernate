package view;

import app.TableManager;
import enums.DiningOption;
import model.Category;
import model.Product;
import repository.RestaurantRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Order extends JPanel {
    private JPanel diningOptionPanel;
    private JPanel tablePanel;
    private JPanel orderPanel;
    private JPanel categoriesAndProductsPanel;
    private JPanel categoriesPanel;
    private JPanel productsPanel;
    private JPanel orderSummaryPanel;
    private TableManager tableManager;
    private RestaurantRepository repository;

    private Map<String, List<OrderItem>> tableOrders;
    private String currentTable;

    public Order() {
        setLayout(new BorderLayout());

        // Initialize TableManager
        tableManager = new TableManager();
        repository = new RestaurantRepository();

        // Initialize the table orders map
        tableOrders = new HashMap<>();

        // Initialize the panels
        diningOptionPanel = new JPanel();
        tablePanel = new JPanel();
        orderPanel = new JPanel();
        categoriesAndProductsPanel = new JPanel(new BorderLayout());
        categoriesPanel = new JPanel();
        productsPanel = new JPanel();
        orderSummaryPanel = new JPanel();

        // Add some components for visualization
        diningOptionPanel.setBorder(BorderFactory.createTitledBorder("Dining Option"));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Table Selection"));
        orderPanel.setBorder(BorderFactory.createTitledBorder("Order Panel"));
        categoriesAndProductsPanel.setBorder(BorderFactory.createTitledBorder("Categories & Products"));
        categoriesPanel.setBorder(BorderFactory.createTitledBorder("Categories"));
        productsPanel.setBorder(BorderFactory.createTitledBorder("Products"));
        orderSummaryPanel.setBorder(BorderFactory.createTitledBorder("Order Summary"));

        // Set layout for the order panel
        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));

        // Populate dining options
        for (DiningOption option : DiningOption.values()) {
            JButton optionButton = new JButton(option.name().replace("_", " "));
            diningOptionPanel.add(optionButton);
            optionButton.addActionListener(new DiningOptionListener(option));
            optionButton.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    optionButton.setBackground(Color.GREEN);
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    optionButton.setBackground(UIManager.getColor("control"));
                }
            });
        }

        // Populate categories
        List<Category> categories = repository.getAllCategories();
        for (Category category : categories) {
            JButton categoryButton = new JButton(category.getName());
            categoriesPanel.add(categoryButton);
            categoryButton.addActionListener(new CategoryButtonListener(category));
        }

        JButton confirmOrderButton = new JButton("Confirm Order");
        JButton cancelOrderButton = new JButton("Cancel Order");
        JButton markAsPaidButton = new JButton("Mark As Paid");
        JButton printButton = new JButton("Print");
        JButton exitButton = new JButton("Exit");

        orderSummaryPanel.add(confirmOrderButton);
        orderSummaryPanel.add(cancelOrderButton);
        orderSummaryPanel.add(markAsPaidButton);
        orderSummaryPanel.add(printButton);
        orderSummaryPanel.add(exitButton);

        // Add ActionListeners
        confirmOrderButton.addActionListener(e -> confirmOrder());
        cancelOrderButton.addActionListener(e -> cancelOrder());
        markAsPaidButton.addActionListener(e -> markAsPaid());
        printButton.addActionListener(e -> printOrder());
        // Add the panels to the layout
        categoriesAndProductsPanel.add(categoriesPanel, BorderLayout.NORTH);
        categoriesAndProductsPanel.add(productsPanel, BorderLayout.CENTER);

        add(diningOptionPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);  // Initially add tablePanel
        add(categoriesAndProductsPanel, BorderLayout.WEST);
        add(orderSummaryPanel, BorderLayout.SOUTH);

        // Add ActionListener for Confirm Order button
        confirmOrderButton.addActionListener(
                e -> {
                    // Replace orderPanel with tablePanel
                    remove(orderPanel);
                    add(tablePanel, BorderLayout.CENTER);

                    // Change the color of the table button to indicate the order status

                    revalidate();
                    repaint();
                }
        );
    }

    private void confirmOrder() {
        // Replace orderPanel with tablePanel
        remove(orderPanel);
        add(tablePanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    // Method to handle Cancel Order
    private void cancelOrder() {
        // Clear the order for the current table
        tableOrders.remove(currentTable);

        // Clear the order panel
        orderPanel.removeAll();
        revalidate();
        repaint();

        // Reset the table to its default state
        tableManager.setTableBusy(tableManager.getDiningOption(currentTable), currentTable, false);

        // Find the table button and reset its color
        for (Component comp : tablePanel.getComponents()) {
            if (comp instanceof JButton && ((JButton) comp).getText().contains(currentTable.split("_")[2])) {
                comp.setBackground(null); // Reset to default color
            }
        }
    }

    // Method to handle Mark As Paid
    private void markAsPaid() {
        // Set the table color to green
        for (Component comp : tablePanel.getComponents()) {
            if (comp instanceof JButton && ((JButton) comp).getText().contains(currentTable.split("_")[2])) {
                comp.setBackground(Color.GREEN);
                break;
            }
        }

        // After 3 seconds, reset the table color
        new Timer(3000, e -> {
            for (Component comp : tablePanel.getComponents()) {
                if (comp instanceof JButton && ((JButton) comp).getText().contains(currentTable.split("_")[2])) {
                    comp.setBackground(null); // Reset to default color
                    break;
                }
            }
        }).start();

        List<OrderItem> orderItems = tableOrders.get(currentTable);
        if (orderItems == null || orderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No order to print!");
            return;
        }
        StringBuilder customerReceipt = new StringBuilder("Customer Slip:\n");
        for (OrderItem item : orderItems) {
            String orderLine = item.getProduct().getName() + " x " + item.getQuantity() + "\n";
            customerReceipt.append(orderLine);
        }

        JOptionPane.showMessageDialog(this, customerReceipt.toString(), "Receipt", JOptionPane.INFORMATION_MESSAGE);
    }

    // Method to handle Print Order
    private void printOrder() {
        List<OrderItem> orderItems = tableOrders.get(currentTable);
        if (orderItems == null || orderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No order to print!");
            return;
        }

        // Simulate printing the order confirmation receipt and kitchen slip
        StringBuilder receipt = new StringBuilder("Order Confirmation:\n");
        StringBuilder kitchenSlip = new StringBuilder("Kitchen Slip:\n");

        for (OrderItem item : orderItems) {
            String orderLine = item.getProduct().getName() + " x " + item.getQuantity() + "\n";
            receipt.append(orderLine);
            kitchenSlip.append(orderLine);
        }

        JOptionPane.showMessageDialog(this, receipt.toString(), "Receipt", JOptionPane.INFORMATION_MESSAGE);
        JOptionPane.showMessageDialog(this, kitchenSlip.toString(), "Kitchen Slip", JOptionPane.INFORMATION_MESSAGE);
    }
    // Listener to handle dining option selection
    private class DiningOptionListener implements ActionListener {
        private DiningOption selectedOption;

        public DiningOptionListener(DiningOption option) {
            this.selectedOption = option;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            tablePanel.removeAll(); // Clear previous tables

            // Add buttons for tables based on the selected dining option
            for (int i = 1; i <= 25; i++) {
                String tableName = selectedOption.name() + "_Table_" + i;
                JButton tableButton = new JButton("Table " + i);
                if (tableManager.isTableBusy(selectedOption, tableName)) {
                    tableButton.setBackground(Color.RED); // Indicate busy tables
                }
                tableButton.addActionListener(new TableButtonListener(selectedOption, tableName, tableButton));
                tablePanel.add(tableButton);
            }

            revalidate();
            repaint();
        }
    }

    // Listener to handle table button actions
    private class TableButtonListener implements ActionListener {
        private DiningOption diningOption;
        private String tableName;
        private JButton tableButton;

        public TableButtonListener(DiningOption diningOption, String tableName, JButton tableButton) {
            this.diningOption = diningOption;
            this.tableName = tableName;
            this.tableButton = tableButton;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            currentTable = tableName;

            // Replace tablePanel with orderPanel
            remove(tablePanel);
            add(orderPanel, BorderLayout.CENTER);

            // Load existing order for the selected table
            loadOrderForTable();

            revalidate();
            repaint();

            // Set the table as busy
            tableManager.setTableBusy(diningOption, tableName, true);
            tableButton.setBackground(Color.RED); // Update button color to indicate it's busy
        }
    }

    // Listener to handle category button actions
    private class CategoryButtonListener implements ActionListener {
        private Category category;

        public CategoryButtonListener(Category category) {
            this.category = category;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Clear previous products
            productsPanel.removeAll();

            // Add products based on the selected category
            List<Product> products = repository.getProductsByCategory(category.getName());
            for (Product product : products) {
                JButton productButton = new JButton(product.getName());
                productsPanel.add(productButton);

                // Add ActionListener to the product button to add the product to the order
                productButton.addActionListener(new ProductButtonListener(product));
            }

            revalidate();
            repaint();
        }
    }

    // Listener to handle product button actions
    private class ProductButtonListener implements ActionListener {
        private Product product;

        public ProductButtonListener(Product product) {
            this.product = product;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            addProductToOrder(product);
        }
    }

    // Add a product to the order
    private void addProductToOrder(Product product) {
        List<OrderItem> orderItems = tableOrders.getOrDefault(currentTable, new ArrayList<>());
        boolean productExists = false;

        for (OrderItem item : orderItems) {
            if (item.getProduct().equals(product)) {
                item.incrementQuantity();
                productExists = true;
                break;
            }
        }

        if (!productExists) {
            orderItems.add(new OrderItem(product, 1));
        }

        tableOrders.put(currentTable, orderItems);
        loadOrderForTable();
    }

    // Load the order for the current table
    private void loadOrderForTable() {
        orderPanel.removeAll();

        List<OrderItem> orderItems = tableOrders.get(currentTable);
        if (orderItems != null) {
            for (OrderItem item : orderItems) {
                JPanel productPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JLabel productLabel = new JLabel(item.getProduct().getName() + " x " + item.getQuantity());
                JButton increaseButton = new JButton("+");
                JButton decreaseButton = new JButton("-");
                JButton removeButton = new JButton("Remove");

                increaseButton.addActionListener(e -> {
                    item.incrementQuantity();
                    loadOrderForTable();
                });

                decreaseButton.addActionListener(e -> {
                    item.decrementQuantity();
                    if (item.getQuantity() <= 0) {
                        orderItems.remove(item);
                    }
                    loadOrderForTable();
                });

                removeButton.addActionListener(e -> {
                    orderItems.remove(item);
                    loadOrderForTable();
                });

                productPanel.add(productLabel);
                productPanel.add(increaseButton);
                productPanel.add(decreaseButton);
                productPanel.add(removeButton);
                orderPanel.add(productPanel);
            }
        }

        revalidate();
        repaint();
    }

    // Main method to run the GUI
    public static void main(String[] args) {
        JFrame frame = new JFrame("Order Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(new Order());
        frame.setVisible(true);
    }

    // Inner class to represent an order item
    private static class OrderItem {
        private Product product;
        private int quantity;

        public OrderItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }

        public void incrementQuantity() {
            quantity++;
        }

        public void decrementQuantity() {
            quantity--;
        }
    }


}
