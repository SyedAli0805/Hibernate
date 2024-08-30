import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.List;

public class RestaurantRepository {

    private static SessionFactory sessionFactory;

    public RestaurantRepository() {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration().configure()
                    .addAnnotatedClass(Product.class)
                    .addAnnotatedClass(Category.class)
                    .addAnnotatedClass(RawMaterial.class)
                    .addAnnotatedClass(Recipe.class)
                    .addAnnotatedClass(RecipeToRawMaterial.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        }
    }

    public Session openSession() {
        return sessionFactory.openSession();
    }

    public List<RawMaterial> getAllRawMaterials() {
        List<RawMaterial> rawMaterials = null;
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            rawMaterials = session.createQuery("from RawMaterial", RawMaterial.class).list();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rawMaterials;
    }

    public List<Category> getAllCategories() {
        List<Category> categories = null;
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            categories = session.createQuery("from Category", Category.class).list();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categories;
    }

    public List<Product> getProductsByCategory(String categoryName) {
        List<Product> products = null;
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            Category category = session.createQuery("from Category where name = :name", Category.class)
                    .setParameter("name", categoryName)
                    .uniqueResult();

            if (category != null) {
                Hibernate.initialize(category.getProducts());
                products = category.getProducts();
            }
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    public Product getProductByName(String name) {
        Product product = null;
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            product = session.createQuery("from Product where name = :name", Product.class)
                    .setParameter("name", name)
                    .uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return product;
    }

    public Category getCategoryByName(String name) {
        Category category = null;
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            category = session.createQuery("from Category where name = :name", Category.class)
                    .setParameter("name", name)
                    .uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return category;
    }

    public RawMaterial getRawMaterialByName(String name) {
        RawMaterial material = null;
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            material = session.createQuery("from RawMaterial where name = :name", RawMaterial.class)
                    .setParameter("name", name)
                    .uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return material;
    }

    public RawMaterial addRawMaterial(String name, double quantity, Unit unit) {
        RawMaterial material = new RawMaterial();
        material.setName(name);
        material.setQuantity(quantity);
        material.setUnit(unit);
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(material);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return material;
    }

    public Product addProduct(String name, double price, ProductSize size, String description, Category category) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setProductSize(size);
        product.setDescription(description);
        product.setCategory(category);
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(product);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return product;
    }

    public Recipe addRecipe(String name, Product product, List<RecipeToRawMaterial> materials) {
        Recipe recipe = new Recipe();
        recipe.setName(name);
        recipe.setProduct(product);
        recipe.setMaterials(materials);
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(recipe);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recipe;
    }

    public RecipeToRawMaterial addRecipeToRawMaterial(Recipe recipe, RawMaterial rawMaterial, int quantity) {
        RecipeToRawMaterial recipeToRawMaterial = new RecipeToRawMaterial();
        recipeToRawMaterial.setRecipe(recipe);
        recipeToRawMaterial.setRawMaterial(rawMaterial);
        recipeToRawMaterial.setQuantity(quantity);
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(recipeToRawMaterial);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recipeToRawMaterial;
    }

    public Category addCategory(String name) {
        Category category = new Category();
        category.setName(name);
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(category);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return category;
    }

    public Product updateProduct(int productId, List<RawMaterial> newMaterials) {
        Product product = null;
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            product = session.get(Product.class, productId);

            if (product != null) {
                session.merge(product);
            }

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return product;
    }

    public void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }
}
