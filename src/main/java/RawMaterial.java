import jakarta.persistence.*;
import java.util.List;

@Entity
public class RawMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private double quantity;

    private Unit unit;

    @OneToMany(mappedBy = "rawMaterial", cascade = CascadeType.ALL)
    private List<RecipeToRawMaterial> recipeMaterials;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public List<RecipeToRawMaterial> getRecipeMaterials() {
        return recipeMaterials;
    }

    public void setRecipeMaterials(List<RecipeToRawMaterial> recipeMaterials) {
        this.recipeMaterials = recipeMaterials;
    }
}
