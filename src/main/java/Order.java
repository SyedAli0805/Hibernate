import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Order {
    private int id;
    private Table table;
    private double totalPrice;
    private LocalDateTime orderDateTime;
    private Map<Product, Integer> products;

    public Order(int id, Table table) {
        this.id = id;
        this.table = table;
        this.orderDateTime = LocalDateTime.now();
        this.products = new HashMap<>();
        this.totalPrice = 0.0;
    }

    public void addProduct(Product product, int quantity) {
        this.products.put(product, this.products.getOrDefault(product, 0) + quantity);
        this.totalPrice += product.getPrice() * quantity;
        table.addProduct(product, quantity);
    }

    public void removeProduct(Product product) {
        if (this.products.containsKey(product)) {
            int quantity = this.products.get(product);
            this.totalPrice -= product.getPrice() * quantity;
            this.products.remove(product);
            table.removeProduct(product);
        }
    }

    public void cancelOrder() {
        this.products.clear();
        this.totalPrice = 0.0;
        table.setBusy(false);
    }

    public void completeOrder() {
        table.setBusy(false);
        generateOrderSlip();
    }

    public void generateOrderSlip() {
        System.out.println("Order Slip:");
        System.out.println("Order ID: " + id);
        System.out.println("Dining Option: " + table.getOption());
        System.out.println("Table: " + table.getName());
        System.out.println("Order Date and Time: " + orderDateTime);
        System.out.println("Products:");
        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            System.out.println("Product: " + entry.getKey().getName() + ", Quantity: " + entry.getValue());
        }
        System.out.println("Total Price: " + totalPrice);
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public Table getTable() {
        return table;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }
}
