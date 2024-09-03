package view;

import enums.ProductSize;
import model.Category;
import model.Product;
import repository.RestaurantRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Objects;

import javax.swing.table.DefaultTableModel;

public class ProductsPage extends JPanel {
    private JTable productsTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton;
    private RestaurantRepository repository;

    public ProductsPage(Dashboard dashboard, String action) {
        repository = new RestaurantRepository();
        setLayout(new BorderLayout());

        // Initialize table model and JTable
        tableModel = new DefaultTableModel(new Object[]{"Name", "Description", "Price", "Size", "Category"}, 0);
        productsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add New Product");
        editButton = new JButton("Edit Product");
        deleteButton = new JButton("Delete Product");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Load products from database
        loadProducts();

        // Add Action Listeners
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showProductForm("Add", null);
            }
        });

        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = productsTable.getSelectedRow();
                if (selectedRow != -1) {
                    String name = (String) tableModel.getValueAt(selectedRow, 0);
                    String description = (String) tableModel.getValueAt(selectedRow, 1);
                    Double price = (Double) tableModel.getValueAt(selectedRow, 2);
                    String size = (String) tableModel.getValueAt(selectedRow, 3);
                    String category = (String) tableModel.getValueAt(selectedRow, 4);
                    showProductForm("Edit", new String[]{name, description, String.valueOf(price), size, category});
                } else {
                    JOptionPane.showMessageDialog(dashboard, "Please select a product to edit.");
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = productsTable.getSelectedRow();
                if (selectedRow != -1) {
                    String name = (String) tableModel.getValueAt(selectedRow, 0);
                    Product product = repository.getProductByName(name);
                    if (product != null) {
                        repository.deleteProduct(product.getId());
                        // Remove from table
                        tableModel.removeRow(selectedRow);
                    }
                } else {
                    JOptionPane.showMessageDialog(dashboard, "Please select a product to delete.");
                }
            }
        });
    }

    private void loadProducts() {
        List<Product> products = repository.getAllProducts();

        // Clear existing rows
        tableModel.setRowCount(0);

        // Add products to the table model
        for (Product product : products) {
            tableModel.addRow(new Object[]{
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getProductSize().name(),
                    product.getCategory().getName()
            });
        }
    }

    private void showProductForm(String action, String[] data) {
        JTextField productNameField = new JTextField();
        JTextField productDescriptionField = new JTextField();
        JTextField productPriceField = new JTextField();
        JComboBox<String> productSizeComboBox = new JComboBox<>();
        JComboBox<String> categoryComboBox = new JComboBox<>();

        List<Category> categories = repository.getAllCategories();
        List<ProductSize> productSizes = List.of(ProductSize.values());

        for (Category category : categories) {
            categoryComboBox.addItem(category.getName());
        }

        for (ProductSize size : productSizes) {
            productSizeComboBox.addItem(size.name());
        }
        if (data != null) {
            productNameField.setText(data[0]);
            productDescriptionField.setText(data[1]);
            productPriceField.setText(data[2]);
            productSizeComboBox.setSelectedItem(data[3]);
            categoryComboBox.setSelectedItem(data[4]);
        }

        Object[] message = {
                "Product Name:", productNameField,
                "Product Description:", productDescriptionField,
                "Product Price:", productPriceField,
                "Product Size:", productSizeComboBox,
                "Product Category:", categoryComboBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, action + " Product", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String productName = productNameField.getText();
            String productDescription = productDescriptionField.getText();
            Double productPrice = Double.valueOf(productPriceField.getText());
            String productSize = Objects.requireNonNull(productSizeComboBox.getSelectedItem()).toString();
            String productCategory = Objects.requireNonNull(categoryComboBox.getSelectedItem()).toString();

            // Convert price to double
            double price;
            try {
                price = Double.parseDouble(String.valueOf(productPrice));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid price format.");
                return;
            }

            Category category = repository.getCategoryByName(productCategory);

            if (category == null) {
                JOptionPane.showMessageDialog(this, "Invalid category.");
                return;
            }

            if (action.equals("Add")) {
                // Add to database
                repository.addProduct(productName, price, ProductSize.valueOf(productSize), productDescription, category);
                tableModel.addRow(new Object[]{productName, productDescription, productPrice, productSize, productCategory});
            } else if (action.equals("Edit")) {
                int selectedRow = productsTable.getSelectedRow();
                if (selectedRow != -1) {
                    String oldName = (String) tableModel.getValueAt(selectedRow, 0);
                    Product existingProduct = repository.getProductByName(oldName);

                    if (existingProduct != null) {
                        // Update in database
                        existingProduct.setName(productName);
                        existingProduct.setDescription(productDescription);
                        existingProduct.setPrice(price);
                        existingProduct.setProductSize(ProductSize.valueOf(productSize));
                        existingProduct.setCategory(category);

                        repository.updateProduct(existingProduct);

                        // Update table
                        tableModel.setValueAt(productName, selectedRow, 0);
                        tableModel.setValueAt(productDescription, selectedRow, 1);
                        tableModel.setValueAt(productPrice, selectedRow, 2);
                        tableModel.setValueAt(productSize, selectedRow, 3);
                        tableModel.setValueAt(productCategory, selectedRow, 4);
                    } else {
                        JOptionPane.showMessageDialog(this, "Product not found in database.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a product to edit.");
                }
            }
        }
    }
}
