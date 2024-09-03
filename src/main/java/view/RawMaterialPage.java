package view;


import enums.Unit;
import model.RawMaterial;
import repository.RestaurantRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Objects;

import javax.swing.table.DefaultTableModel;

public class RawMaterialPage extends JPanel {
    private JTable rawMaterialTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton;
    private RestaurantRepository repository;

    public RawMaterialPage(Dashboard dashboard, String action) {
        repository = new RestaurantRepository();
        setLayout(new BorderLayout());

        // Initialize table model and JTable
        tableModel = new DefaultTableModel(new Object[]{"Name", "Quantity", "Unit"}, 0);
        rawMaterialTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(rawMaterialTable);
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

        // Load raw materials from database
        loadRawMaterials();

        // Add Action Listeners
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showRawMaterialForm("Add", null);
            }
        });

        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = rawMaterialTable.getSelectedRow();
                if (selectedRow != -1) {
                    String name = (String) tableModel.getValueAt(selectedRow, 0);
                    Double quantity = (Double) tableModel.getValueAt(selectedRow, 1);
                    String unit = (String) tableModel.getValueAt(selectedRow, 2);
                    showRawMaterialForm("Edit", new String[]{name, String.valueOf(quantity), unit});
                } else {
                    JOptionPane.showMessageDialog(dashboard, "Please select a raw material to edit.");
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = rawMaterialTable.getSelectedRow();
                if (selectedRow != -1) {
                    String name = (String) tableModel.getValueAt(selectedRow, 0);
                    RawMaterial rawMaterial = repository.getRawMaterialByName(name);
                    if (rawMaterial != null) {
                        repository.deleteRawMaterial(rawMaterial.getId());
                        // Remove from table
                        tableModel.removeRow(selectedRow);
                    }
                } else {
                    JOptionPane.showMessageDialog(dashboard, "Please select a product to delete.");
                }
            }
        });
    }

    private void loadRawMaterials() {
        List<RawMaterial> materials = repository.getAllRawMaterials();

        // Clear existing rows
        tableModel.setRowCount(0);

        // Add products to the table model
        for (RawMaterial rawMaterial : materials) {
            tableModel.addRow(new Object[]{
                    rawMaterial.getName(),
                    rawMaterial.getQuantity(),
                    rawMaterial.getUnit().name()
            });
        }
    }

    private void showRawMaterialForm(String action, String[] data) {
        JTextField rawMaterialNameField = new JTextField();
        JTextField rawMaterialQuantityField = new JTextField();
        JComboBox<String> rawMaterialUnitComboBox = new JComboBox<>();

        // Populate combo box with Unit enum values
        List<Unit> rawMaterialUnits = List.of(Unit.values());
        for (Unit unit : rawMaterialUnits) {
            rawMaterialUnitComboBox.addItem(unit.name());
        }

        // Pre-fill form fields if data is provided
        if (data != null) {
            rawMaterialNameField.setText(data[0]);
            rawMaterialQuantityField.setText(data[1]);
            rawMaterialUnitComboBox.setSelectedItem(data[2]);
        }

        Object[] message = {
                "Raw Material Name:", rawMaterialNameField,
                "Raw Material Quantity:", rawMaterialQuantityField,
                "Raw Material Unit:", rawMaterialUnitComboBox,
        };

        int option = JOptionPane.showConfirmDialog(this, message, action + " Raw Material", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String rawMaterialName = rawMaterialNameField.getText();
            double rawMaterialQuantity = Double.parseDouble(rawMaterialQuantityField.getText());
            String rawMaterialUnit = Objects.requireNonNull(rawMaterialUnitComboBox.getSelectedItem()).toString();

            // Convert string to enum
            Unit unit;
            try {
                unit = Unit.valueOf(rawMaterialUnit);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Invalid unit selected.");
                return;
            }

            if (action.equals("Add")) {
                // Add to database
                repository.addRawMaterial(rawMaterialName, rawMaterialQuantity, unit);
                tableModel.addRow(new Object[]{rawMaterialName, rawMaterialQuantity,rawMaterialUnit});
            } else if (action.equals("Edit")) {
                int selectedRow = rawMaterialTable.getSelectedRow();
                if (selectedRow != -1) {
                    String oldName = (String) tableModel.getValueAt(selectedRow, 0);
                    RawMaterial existingRawMaterial = repository.getRawMaterialByName(oldName);

                    if (existingRawMaterial != null) {
                        // Update in database
                        existingRawMaterial.setName(rawMaterialName);
                        existingRawMaterial.setQuantity(rawMaterialQuantity);
                        existingRawMaterial.setUnit(Unit.valueOf(rawMaterialUnit));
                        repository.updateRawMaterial(existingRawMaterial);

                        // Update table
                        tableModel.setValueAt(rawMaterialName, selectedRow, 0);
                        tableModel.setValueAt(rawMaterialQuantity, selectedRow, 1);
                        tableModel.setValueAt(rawMaterialUnit, selectedRow, 2);
                    } else {
                        JOptionPane.showMessageDialog(this, "Raw Material not found in database.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a raw material to edit.");
                }
            }
        }
    }

}
