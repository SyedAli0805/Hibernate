package model;



import enums.DiningOption;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "restaurant_table")
public class RestaurantTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String tableName;
    private boolean isBusy;
    private DiningOption diningOption;

    @OneToMany(mappedBy = "restaurantTable")
    private List<Order> orders;

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public DiningOption getDiningOption() {
        return diningOption;
    }

    public void setDiningOption(DiningOption diningOption) {
        this.diningOption = diningOption;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}

