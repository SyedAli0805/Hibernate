import jakarta.persistence.Entity;

import java.util.HashMap;
import java.util.Map;


public class Table {
    private String name;
    private DiningOption option;
    private boolean isBusy;
    private Map<Product, Integer> selectedProducts;

    public Table(String name, DiningOption option) {
        this.name = name;
        this.option = option;
        this.isBusy = false;
        this.selectedProducts = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public DiningOption getOption() {
        return option;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public Map<Product, Integer> getSelectedProducts() {
        return selectedProducts;
    }

    public void addProduct(Product product, int quantity) {
        this.selectedProducts.put(product, this.selectedProducts.getOrDefault(product, 0) + quantity);
    }

    public void removeProduct(Product product) {
        this.selectedProducts.remove(product);
    }
}
