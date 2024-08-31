package model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToOne
    private Product product;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    private List<RecipeToRawMaterial> materials;

    // Getters and Setters
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<RecipeToRawMaterial> getMaterials() {
        return materials;
    }

    public void setMaterials(List<RecipeToRawMaterial> materials) {
        this.materials = materials;
    }
}
