package app;

import enums.DiningOption;
import model.Order;
import model.Product;
import repository.RestaurantRepository;

import java.util.ArrayList;
import java.util.List;


public class App {
    public static void main(String[] args) {
        RestaurantRepository repository = new RestaurantRepository();

        // Add some categories
        model.Category category1 = repository.addCategory( "Beverages");
        model.Category category2 = repository.addCategory("Desserts");

        // Add some raw materials
        model.RawMaterial milk = repository.addRawMaterial("Milk", 1000, enums.Unit.LITER);
        model.RawMaterial sugar = repository.addRawMaterial( "Sugar", 500, enums.Unit.KILOGRAMS);

        // Add a product
        model.Product product = repository.addProduct("Milkshake", 5.0, enums.ProductSize.MEDIUM, "Delicious milkshake", category1);
        model.Product product1 = repository.addProduct("Coca Cola", 2.5, enums.ProductSize.HALF_LITRE, "Chilled Coca Cola", category1);

        // Create a recipe and associate raw materials
        model.Recipe recipe = repository.addRecipe( "Milkshake", product, new ArrayList<>());
        repository.addRecipeToRawMaterial( recipe, milk, 200,enums.Unit.GRAMS);
        repository.addRecipeToRawMaterial( recipe, sugar, 50,enums.Unit.GRAMS);








        Product product2 = repository.getProductByName("Coca Cola");
        List<Product> products =  new ArrayList<>();
        products.add(product2);
        Order order = repository.addOrder(products,"DINE_IN_Table_1", DiningOption.DINE_IN);

        repository.payOrder(order);

        // Close the session factory
        repository.closeSessionFactory();
    }
}
