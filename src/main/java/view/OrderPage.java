package view;

import enums.DiningOption;
import model.Category;
import model.Product;
import model.RestaurantTable;
import repository.RestaurantRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderPage extends JPanel {
    private JPanel diningOptionPanel;
    private JPanel tablePanel;
    private JPanel orderPanel;
    private JPanel categoriesAndProductsPanel;
    private JPanel categoriesPanel;
    private JPanel productsPanel;
    private JPanel orderSummaryPanel;
    private RestaurantRepository repository;

    private Map<String, List<OrderItem>> tableOrders; // Keeps track of orders for each table
    private String currentTable;

    public OrderPage(Dashboard dashboard) {
        setLayout(new BorderLayout());

        // Initialize Repository
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

        // Set up the UI and add components
        setUpUI();

        // Populate categories
        populateCategories();
    }

    private void setUpUI() {
        // Add borders for visualization
        diningOptionPanel.setBorder(BorderFactory.createTitledBorder("Dining Option"));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Table Selection"));
        orderPanel.setBorder(BorderFactory.createTitledBorder("OrderPage Panel"));
        categoriesAndProductsPanel.setBorder(BorderFactory.createTitledBorder("Categories & Products"));
        categoriesPanel.setBorder(BorderFactory.createTitledBorder("Categories"));
        productsPanel.setBorder(BorderFactory.createTitledBorder("Products"));
        orderSummaryPanel.setBorder(BorderFactory.createTitledBorder("OrderPage Summary"));

        // Set layout for the order panel
        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));

        // Populate dining options
        for (DiningOption option : DiningOption.values()) {
            JButton optionButton = new JButton(option.name().replace("_", " "));
            diningOptionPanel.add(optionButton);
            optionButton.addActionListener(new DiningOptionListener(option));
        }

        // OrderPage summary buttons
        JButton confirmOrderButton = new JButton("Confirm OrderPage");
        JButton cancelOrderButton = new JButton("Cancel OrderPage");
        JButton markAsPaidButton = new JButton("Mark As Paid");
        JButton printButton = new JButton("Print");
        JButton exitButton = new JButton("Exit");

        orderSummaryPanel.add(confirmOrderButton);
        orderSummaryPanel.add(cancelOrderButton);
        orderSummaryPanel.add(markAsPaidButton);
        orderSummaryPanel.add(printButton);
        orderSummaryPanel.add(exitButton);

        // Add ActionListeners for OrderPage actions
        confirmOrderButton.addActionListener(e -> confirmOrder());
        cancelOrderButton.addActionListener(e -> cancelOrder());
        markAsPaidButton.addActionListener(e -> markAsPaid());
        printButton.addActionListener(e -> printOrder());

        // Add the panels to the layout
        categoriesAndProductsPanel.add(categoriesPanel, BorderLayout.NORTH);
        categoriesAndProductsPanel.add(productsPanel, BorderLayout.CENTER);

        add(diningOptionPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(categoriesAndProductsPanel, BorderLayout.WEST);
        add(orderSummaryPanel, BorderLayout.SOUTH);
    }

    private void populateCategories() {
        List<Category> categories = repository.getAllCategories();
        for (Category category : categories) {
            JButton categoryButton = new JButton(category.getName());
            categoriesPanel.add(categoryButton);
            categoryButton.addActionListener(new CategoryButtonListener(category));
        }
    }

    private void confirmOrder() {
        if (currentTable == null) {
            JOptionPane.showMessageDialog(this, "No table selected for the order!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<OrderItem> orderItems = tableOrders.get(currentTable);
        if (orderItems == null || orderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No items in the order!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Prepare order details
        double totalPrice = 0.0;
        List<Product> products = new ArrayList<>();
        for (OrderItem item : orderItems) {
            totalPrice += item.getQuantity() * item.getProduct().getPrice();
            products.add(item.getProduct());
        }

        // Fetch the selected table from the repository
        RestaurantTable restaurantTable = repository.getTableByName(currentTable);

        // Add order to the repository
        repository.addOrder(restaurantTable, totalPrice, enums.OrderStatus.PLACED, products);

        JOptionPane.showMessageDialog(this, "OrderPage confirmed for " + currentTable);

        // Switch back to the table selection panel
        remove(orderPanel);
        add(tablePanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // Handle Cancel OrderPage
    private void cancelOrder() {
        // Remove the current order from the tableOrders map
        tableOrders.remove(currentTable);

        // Free up the table in the repository
        RestaurantTable table = repository.getTableByName(currentTable);
        model.Order order = repository.getOrderByTable(table);
        repository.cancelOrder(order);  // Use the OrderPage object to cancel

        // Clear the order panel
        orderPanel.removeAll();
        revalidate();
        repaint();

        JOptionPane.showMessageDialog(this, "OrderPage canceled for " + currentTable);
    }

    // Handle Mark As Paid
    private void markAsPaid() {
        List<OrderItem> orderItems = tableOrders.get(currentTable);
        if (orderItems == null || orderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No order to mark as paid!");
            return;
        }

        // Set table as free after payment
        RestaurantTable table = repository.getTableByName(currentTable);
        model.Order order = repository.getOrderByTable(table);
        repository.payOrder(order);  // Mark the order as paid in the repository

        // Update the table UI to show it is free
        updateTableStatus(Color.GREEN);
    }

    private void printOrder() {
        List<OrderItem> orderItems = tableOrders.get(currentTable);
        if (orderItems == null || orderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No items in the order!", "Error", JOptionPane.ERROR_MESSAGE);
        }else {
            RestaurantTable table = repository.getTableByName(currentTable);
            model.Order order = repository.getOrderByTable(table);
            JOptionPane.showMessageDialog(this, "OrderPage printed for " + currentTable);
        }
    }

    // Update the status of the current table (for visual feedback)
    private void updateTableStatus(Color color) {
        for (Component comp : tablePanel.getComponents()) {
            if (comp instanceof JButton && ((JButton) comp).getText().contains(currentTable.split("_")[2])) {
                comp.setBackground(color);
            }
        }
    }

    // Listener for selecting a dining option
    private class DiningOptionListener implements ActionListener {
        private DiningOption selectedOption;

        public DiningOptionListener(DiningOption option) {
            this.selectedOption = option;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            tablePanel.removeAll();

            // Fetch tables for the selected dining option from the repository
            List<RestaurantTable> tables = repository.getTablesByDiningOption(selectedOption);

            for (RestaurantTable table : tables) {
                JButton tableButton = new JButton(table.getTableName());
                if (table.isBusy()) {
                    tableButton.setBackground(Color.RED);  // Show busy tables
                }
                tableButton.addActionListener(new TableButtonListener(selectedOption, table.getTableName(), tableButton));
                tablePanel.add(tableButton);
            }

            revalidate();
            repaint();
        }
    }

    // Listener for selecting a table
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

            // Replace table panel with order panel
            remove(tablePanel);
            add(orderPanel, BorderLayout.CENTER);

            // Load existing order for the selected table
            loadOrderForTable();

            revalidate();
            repaint();

            // Mark the table as busy
            tableButton.setBackground(Color.RED);  // Update table button color
        }
    }

    // Listener for selecting a product category
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

    // Listener for selecting a product to add to the order
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
        loadOrderForTable();  // Reload the order for the current table to display updates
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

