import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class RestaurantApp {

    public RestaurantApp() {
        Restaurant restaurant = new Restaurant();
        Scanner scanner = new Scanner(System.in);
        RestaurantRepository repository = new RestaurantRepository();


            try {
                System.out.println("Select a Dining Option:");
                for (DiningOption option : DiningOption.values()) {
                    System.out.println(option.ordinal() + 1 + ": " + option);
                }
                int optionIndex = scanner.nextInt() - 1;
                DiningOption selectedOption = DiningOption.values()[optionIndex];

                System.out.println("Available Tables for " + selectedOption + ":");
                for (Table table : restaurant.getTablesByDiningOption(selectedOption)) {
                    System.out.println(table.getName() + (table.isBusy() ? " (Busy)" : " (Free)"));
                }

                System.out.print("Select a Table: ");
                String tableName = scanner.next();
                Table selectedTable = null;
                for (Table table : restaurant.getTablesByDiningOption(selectedOption)) {
                    if (table.getName().equals(tableName)) {
                        selectedTable = table;
                        break;
                    }
                }

                if (selectedTable == null || selectedTable.isBusy()) {
                    System.out.println("Invalid Table Selection.");
                }

                selectedTable.setBusy(true);
                Order order = new Order(1, selectedTable);


                    // Fetch and display categories
                    List<Category> categories = repository.getAllCategories();

                    // Fetch products by a specific category
                    List<Product> products = repository.getProductsByCategory("Pizza");

                    if (!products.isEmpty()) {
                        for (Product product : products) {
                            System.out.println(product.getName());
                        }

                        // Fetch specific product by name
                        Product product = repository.getProductByName("Chicken Fajita");

                        if (product != null) {
                            order.addProduct(product, 2);
                        } else {
                            System.out.println("Product not found.");
                        }
                    } else {
                        System.out.println("Invalid Category.");

                    }


                System.out.println("Order Summary:");
                order.generateOrderSlip();

                System.out.println("Complete Order? (yes/no)");
                String complete = scanner.next();
                if (complete.equalsIgnoreCase("yes")) {
                    order.completeOrder();
                    restaurant.releaseTable(selectedTable);
                } else {
                    order.cancelOrder();
                }

            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid option.");
                scanner.next(); // Clear the invalid input
            }

    }
}
