import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        RestaurantRepository repository = new RestaurantRepository();

        // Add some categories
        Category category1 = repository.addCategory( "Beverages");
        Category category2 = repository.addCategory("Desserts");

        // Add some raw materials
        RawMaterial milk = repository.addRawMaterial("Milk", 1000, Unit.LITER);
        RawMaterial sugar = repository.addRawMaterial( "Sugar", 500, Unit.KILOGRAMS);

        // Add a product
        Product product = repository.addProduct("Milkshake", 5.0, ProductSize.MEDIUM, "Delicious milkshake", category1);

        // Create a recipe and associate raw materials
        Recipe recipe = repository.addRecipe( "Milkshake Recipe", product, new ArrayList<>());
        repository.addRecipeToRawMaterial( recipe, milk, 200);
        repository.addRecipeToRawMaterial( recipe, sugar, 50);

        // Close the session factory
        repository.closeSessionFactory();
    }
}
