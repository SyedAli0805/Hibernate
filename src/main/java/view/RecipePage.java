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
import java.util.ArrayList;
import java.util.List;

public class RecipePage extends JPanel {
    private JTable recipeTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton;
    private RestaurantRepository repository;

    public RecipePage(Dashboard dashboard, String action) {
        setLayout(new BorderLayout());
        this.repository = new RestaurantRepository();

        // Initialize table model and JTable
        tableModel = new DefaultTableModel(new Object[]{"Recipe Name", "Product", "Materials", "Quantities", "Units"}, 0);
        recipeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(recipeTable);
        add(scrollPane, BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add New Recipe");
        editButton = new JButton("Edit Recipe");
        deleteButton = new JButton("Delete Recipe");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadRecipesFromDatabase();

        // Add Action Listeners
        addButton.addActionListener(e -> showRecipeForm("Add", null));

        editButton.addActionListener(e -> {
            int selectedRow = recipeTable.getSelectedRow();
            if (selectedRow != -1) {
                String recipeName = (String) tableModel.getValueAt(selectedRow, 0);
                String product = (String) tableModel.getValueAt(selectedRow, 1);
                String materials = (String) tableModel.getValueAt(selectedRow, 2);
                String quantities = (String) tableModel.getValueAt(selectedRow, 3);
                String units = (String) tableModel.getValueAt(selectedRow, 4);
                showRecipeForm("Edit", new String[]{recipeName, product, materials, quantities, units});
            } else {
                JOptionPane.showMessageDialog(dashboard, "Please select a recipe to edit.");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = recipeTable.getSelectedRow();
            if (selectedRow != -1) {
                String recipeName = (String) recipeTable.getValueAt(selectedRow, 0);
                Recipe recipe = repository.getRecipeByName(recipeName);
                if (recipe != null) {
                    repository.deleteRecipe(recipe.getId());
                    tableModel.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(dashboard, "This recipe does not exist.");
                }
            } else {
                JOptionPane.showMessageDialog(dashboard, "Please select a recipe to delete.");
            }
        });
    }

    private void loadRecipesFromDatabase() {
        List<Recipe> recipes = repository.getAllRecipes();
        for (Recipe recipe : recipes) {
            String recipeName = recipe.getName();
            Product product = recipe.getProduct();


            StringBuilder materialsBuilder = new StringBuilder();
            StringBuilder quantitiesBuilder = new StringBuilder();
            StringBuilder unitsBuilder = new StringBuilder();

            for (RecipeToRawMaterial material : recipe.getMaterials()) {
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
                if (column == 0) {
                    JComboBox<String> rawMaterialDropdown = new JComboBox<>();
                    List<RawMaterial> rawMaterials = repository.getAllRawMaterials();
                    for (RawMaterial rawMaterial : rawMaterials) {
                        rawMaterialDropdown.addItem(rawMaterial.getName());
                    }
                    return new DefaultCellEditor(rawMaterialDropdown);
                } else if (column == 2) {
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
                if (column == 0 || column == 2) {
                    return new DefaultTableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                            JComboBox<String> comboBox = new JComboBox<>();
                            if (column == 0) {
                                List<RawMaterial> rawMaterials = repository.getAllRawMaterials();
                                for (RawMaterial rawMaterial : rawMaterials) {
                                    comboBox.addItem(rawMaterial.getName());
                                }
                            } else if (column == 2) {
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

        List<Product> products = repository.getAllProducts();
        for (Product product : products) {
            productDropdown.addItem(product.getName());
        }

        if (data != null) {
            recipeNameField.setText(data[0]);
            productDropdown.setSelectedItem(data[1]);
            String[] materials = data[2].split(",");
            String[] quantities = data[3].split(",");
            String[] units = data[4].split(",");

            int length = Math.min(materials.length, quantities.length);
            for (int i = 0; i < length; i++) {
                materialsModel.addRow(new Object[]{materials[i], quantities[i], units[i]});
            }

            if (materials.length != quantities.length || quantities.length != units.length) {
                JOptionPane.showMessageDialog(this, "Warning: Materials, Quantities, and Units length mismatch!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }

        JPanel materialPanel = new JPanel(new BorderLayout());
        materialPanel.add(new JScrollPane(materialsTable), BorderLayout.CENTER);

        JPanel materialButtonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        JButton addMaterialButton = new JButton("Add Material");
        addMaterialButton.addActionListener(e -> materialsModel.addRow(new Object[]{"", "", ""}));
        JButton removeMaterialButton = new JButton("Remove Material");
        removeMaterialButton.addActionListener(e -> {
            int selectedRow = materialsTable.getSelectedRow();
            if (selectedRow != -1) {
                materialsModel.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a material to remove.");
            }
        });

        materialButtonPanel.add(addMaterialButton);
        materialButtonPanel.add(removeMaterialButton);
        materialPanel.add(materialButtonPanel, BorderLayout.SOUTH);

        Object[] message = {
                "Recipe Name:", recipeNameField,
                "Product:", productDropdown,
                "Materials:", materialPanel
        };

        int option = JOptionPane.showConfirmDialog(this, message, action + " Recipe", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String recipeName = recipeNameField.getText();
            String productName = (String) productDropdown.getSelectedItem();

            if (recipeName.isEmpty() || productName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Recipe Name and Product cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Product selectedProduct = repository.getProductByName(productName);
            Recipe recipe = action.equals("Edit") ? repository.getRecipeByName(recipeName) : new Recipe();
            recipe.setName(recipeName);
            recipe.setProduct(selectedProduct);

            List<RecipeToRawMaterial> updatedMaterials = new ArrayList<>();
            for (int i = 0; i < materialsModel.getRowCount(); i++) {
                String materialName = (String) materialsModel.getValueAt(i, 0);
                String quantityStr = (String) materialsModel.getValueAt(i, 1);
                String unitName = (String) materialsModel.getValueAt(i, 2);

                if (materialName.isEmpty() || quantityStr.isEmpty() || unitName.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Material, Quantity, and Unit cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                RawMaterial rawMaterial = repository.getRawMaterialByName(materialName);
                double quantity;
                try {
                    quantity = Double.parseDouble(quantityStr);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid quantity: " + quantityStr, "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Unit unit = Unit.valueOf(unitName);

                RecipeToRawMaterial recipeToRawMaterial = action.equals("Edit") && i < recipe.getMaterials().size()
                        ? recipe.getMaterials().get(i)
                        : new RecipeToRawMaterial();
                recipeToRawMaterial.setRecipe(recipe);
                recipeToRawMaterial.setRawMaterial(rawMaterial);
                recipeToRawMaterial.setQuantity(quantity);
                recipeToRawMaterial.setUnit(unit);

                updatedMaterials.add(recipeToRawMaterial);
            }

            if (action.equals("Edit")) {
                repository.updateRecipe(recipe.getName(),selectedProduct, updatedMaterials);
                while(tableModel.getRowCount() > 0){
                    tableModel.removeRow(0);
                }
                loadRecipesFromDatabase(); // Reload data from the database
            } else {
                recipe.setMaterials(updatedMaterials);
                repository.addRecipe(recipe.getName(),selectedProduct,updatedMaterials);
                while(tableModel.getRowCount() > 0){
                    tableModel.removeRow(0);
                }
                loadRecipesFromDatabase();
            }
        }
    }
}