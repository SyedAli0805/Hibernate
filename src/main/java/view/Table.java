package view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Table {
    private int number;
    private JButton button;
    private Color color;
    private Map<String, Integer> selectedProducts; // Map to store product names and their quantities

    public Table(int number) {
        this.number = number;
        this.button = new JButton("Table " + number);
        this.color = Color.GREEN; // Default color for an empty table
        this.selectedProducts = new HashMap<>(); // Initialize the map for each table
    }

    public JButton getButton() {
        return button;
    }

    public Color getColor() {
        return color;
    }

    public Map<String, Integer> getSelectedProducts() {
        return selectedProducts;
    }



    public void setColor(Color color) {
        this.color = color;
        button.setBackground(color);
    }

    public void placeOrder() {
        setColor(Color.ORANGE); // Mark as ordered
    }

    public void markAsPaid() {
        setColor(Color.GREEN); // Mark as paid and reset color to default
        selectedProducts.clear(); // Clear the products after payment
    }

    public void resetTable() {
        setColor(Color.GREEN); // Reset color to default
        selectedProducts.clear(); // Clear the products on reset
    }
}
