package view;

import enums.Unit;
import model.Product;
import model.RawMaterial;
import model.Recipe;
import model.RecipeToRawMaterial;
import repository.RestaurantRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class RecipePage extends JPanel {
    private JTable recipeTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, saveButton;
    private RestaurantRepository repository;

    public RecipePage(Dashboard dashboard, String action) {
        setLayout(new BorderLayout());
        this.repository = new RestaurantRepository();

        // Initialize table model and JTable
        tableModel = new DefaultTableModel(new Object[]{"Recipe Name","Product","Materials", "Quantities", "Units"}, 0);
        recipeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(recipeTable);
        add(scrollPane, BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add New Recipe");
        editButton = new JButton("Edit Recipe");
        deleteButton = new JButton("Delete Recipe");
        saveButton = new JButton("Save Recipe");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadRecipesFromDatabase();

        // Add Action Listeners
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showRecipeForm("Add", null);
            }
        });

        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = recipeTable.getSelectedRow();
                if (selectedRow != -1) {
                    String recipeName = (String) tableModel.getValueAt(selectedRow, 0);
                    String materials = (String) tableModel.getValueAt(selectedRow, 1);
                    String quantities = (String) tableModel.getValueAt(selectedRow, 2);
                    String units = (String) tableModel.getValueAt(selectedRow, 3);
                    showRecipeForm("Edit", new String[]{recipeName, materials, quantities, units});
                } else {
                    JOptionPane.showMessageDialog(dashboard, "Please select a recipe to edit.");
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = recipeTable.getSelectedRow();
                if (selectedRow != -1) {
                    tableModel.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(dashboard, "Please select a recipe to delete.");
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Save all the recipes to the database
                saveRecipesToDatabase();
            }
        });
    }

    private void loadRecipesFromDatabase() {
        List<Recipe> recipes = repository.getAllRecipes();
        for (Recipe recipe : recipes) {
            String recipeName = recipe.getName();
            Product product = recipe.getProduct();
            List<RecipeToRawMaterial> recipeMaterials =repository.getAllRecipeToRawMaterials();

            StringBuilder materialsBuilder = new StringBuilder();
            StringBuilder quantitiesBuilder = new StringBuilder();
            StringBuilder unitsBuilder = new StringBuilder();

            for (RecipeToRawMaterial material : recipeMaterials) {
                if (materialsBuilder.length() > 0) {
                    materialsBuilder.append(",");
                    quantitiesBuilder.append(",");
                    unitsBuilder.append(",");
                }
                materialsBuilder.append(material.getRawMaterial().getName());
                quantitiesBuilder.append(material.getQuantity());
                unitsBuilder.append(material.getUnit().name());
            }

            tableModel.addRow(new Object[]{recipeName, product.getName(), materialsBuilder.toString(), quantitiesBuilder.toString(), unitsBuilder.toString()});
        }
    }


    private void showRecipeForm(String action, String[] data) {
        JTextField recipeNameField = new JTextField();
        JComboBox<String> productDropdown = new JComboBox<>();
        DefaultTableModel materialsModel = new DefaultTableModel(new Object[]{"Material", "Quantity", "Unit"}, 0);
        JTable materialsTable = new JTable(materialsModel) {
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if (column == 0) {  // Material column
                    JComboBox<String> rawMaterialDropdown = new JComboBox<>();
                    List<RawMaterial> rawMaterials = repository.getAllRawMaterials();
                    for (RawMaterial rawMaterial : rawMaterials) {
                        rawMaterialDropdown.addItem(rawMaterial.getName());
                    }
                    return new DefaultCellEditor(rawMaterialDropdown);
                } else if (column == 2) {  // Unit column
                    JComboBox<String> unitDropdown = new JComboBox<>();
                    for (Unit unit : Unit.values()) {
                        unitDropdown.addItem(unit.name());
                    }
                    return new DefaultCellEditor(unitDropdown);
                } else {
                    return super.getCellEditor(row, column);
                }
            }

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 0 || column == 2) {  // Material or Unit column
                    return new DefaultTableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                            JComboBox<String> comboBox = new JComboBox<>();
                            if (column == 0) {  // Material column
                                List<RawMaterial> rawMaterials = repository.getAllRawMaterials();
                                for (RawMaterial rawMaterial : rawMaterials) {
                                    comboBox.addItem(rawMaterial.getName());
                                }
                            } else if (column == 2) {  // Unit column
                                for (Unit unit : Unit.values()) {
                                    comboBox.addItem(unit.name());
                                }
                            }
                            comboBox.setSelectedItem(value);
                            return comboBox;
                        }
                    };
                } else {
                    return super.getCellRenderer(row, column);
                }
            }
        };

        // Populate the product dropdown
        List<Product> products = repository.getAllProducts();
        for (Product product : products) {
            productDropdown.addItem(product.getName());
        }

        // If editing, populate the fields
        if (data != null) {
            recipeNameField.setText(data[0]);
            productDropdown.setSelectedItem(data[1]);
            String[] materials = data[2].split(",");
            String[] quantities = data[3].split(",");
            String[] units = data[4].split(",");

            int length = Math.min(materials.length, Math.min(quantities.length, units.length));
            for (int i = 0; i < length; i++) {
                materialsModel.addRow(new Object[]{materials[i], quantities[i], units[i]});
            }

            if (materials.length != quantities.length || quantities.length != units.length) {
                JOptionPane.showMessageDialog(this, "Warning: Materials, Quantities, and Units length mismatch!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }

        // Panel for adding materials
        JPanel materialPanel = new JPanel(new BorderLayout());
        materialPanel.add(new JScrollPane(materialsTable), BorderLayout.CENTER);

        JButton addMaterialButton = new JButton("Add Material");
        addMaterialButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                materialsModel.addRow(new Object[]{"", "", ""});
            }
        });

        materialPanel.add(addMaterialButton, BorderLayout.SOUTH);

        Object[] message = {
                "Recipe Name:", recipeNameField,
                "Product:", productDropdown,
                "Materials and Quantities:", materialPanel
        };

        int option = JOptionPane.showConfirmDialog(this, message, action + " Recipe", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String recipeName = recipeNameField.getText();
            String selectedProductName = (String) productDropdown.getSelectedItem();
            Product selectedProduct = repository.getProductByName(selectedProductName);

            ArrayList<String> materialsList = new ArrayList<>();
            ArrayList<String> quantitiesList = new ArrayList<>();
            ArrayList<String> unitsList = new ArrayList<>();

            for (int i = 0; i < materialsModel.getRowCount(); i++) {
                String material = (String) materialsModel.getValueAt(i, 0);
                String quantity = (String) materialsModel.getValueAt(i, 1);
                String unit = (String) materialsModel.getValueAt(i, 2);

                materialsList.add(material);
                quantitiesList.add(quantity);
                unitsList.add(unit);
            }

            String materials = String.join(",", materialsList);
            String quantities = String.join(",", quantitiesList);
            String units = String.join(",", unitsList);

            if (action.equals("Add")) {
                tableModel.addRow(new Object[]{recipeName, selectedProductName, materials, quantities, units});
            } else if (action.equals("Edit")) {
                int selectedRow = recipeTable.getSelectedRow();
                tableModel.setValueAt(recipeName, selectedRow, 0);
                tableModel.setValueAt(selectedProductName, selectedRow, 1);
                tableModel.setValueAt(materials, selectedRow, 2);
                tableModel.setValueAt(quantities, selectedRow, 3);
                tableModel.setValueAt(units, selectedRow, 4);
            }
        }
    }



    private void saveRecipesToDatabase() {
        // Iterate through all rows in the table
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String recipeName = (String) tableModel.getValueAt(i, 0);
            String[] materials = ((String) tableModel.getValueAt(i, 1)).split(",");
            for (String material : materials) {
                System.out.println(material);
            }
            String[] quantities = ((String) tableModel.getValueAt(i, 2)).split(",");
            for (String quantity : quantities) {
                System.out.println(quantity);
            }
            String[] units = ((String) tableModel.getValueAt(i, 3)).split(",");

            for (String unit: units){
                System.out.println(unit);
            }

            // Create and save recipe
            Product product = repository.getProductByName(recipeName); // Assuming you have a method to find a product by name
            Recipe recipe = repository.addRecipe(recipeName, product, new ArrayList<>());

            // Save each material to the database
            for (int j = 0; j < materials.length; j++) {
                RawMaterial rawMaterial = repository.getRawMaterialByName(materials[j]);
                double quantity = Double.parseDouble(quantities[j]);
                Unit unit = Unit.valueOf(units[j]);

                repository.addRecipeToRawMaterial(recipe, rawMaterial, quantity, unit);
            }
        }

        JOptionPane.showMessageDialog(this, "Recipes saved successfully!");
    }
}
