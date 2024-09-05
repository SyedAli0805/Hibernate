package repository;

import app.TableManager;
import enums.DiningOption;
import enums.OrderStatus;
import enums.ProductSize;
import enums.Unit;
import model.*;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.time.LocalDateTime;
import java.util.List;

public class RestaurantRepository {

    private static SessionFactory sessionFactory;
    private TableManager tableManager;

    public RestaurantRepository() {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration().configure()
                    .addAnnotatedClass(Product.class)
                    .addAnnotatedClass(Category.class)
                    .addAnnotatedClass(RawMaterial.class)
                    .addAnnotatedClass(Recipe.class)
                    .addAnnotatedClass(RecipeToRawMaterial.class)
                    .addAnnotatedClass(Order.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        }
        tableManager  = new TableManager();
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

    public List<Product> getAllProducts() {
        List<Product> products = null;
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            products = session.createQuery("from Product", Product.class).list();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
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

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = null;
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            recipes = session.createQuery("from Recipe ", Recipe.class).list();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recipes;
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


    public Recipe getRecipeByName(String name) {
        Recipe recipe = null;
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            recipe = session.createQuery("from Recipe where name = :name", Recipe.class)
                    .setParameter("name", name)
                    .uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recipe;
    }

    public Order getOrderById(int orderId) {
        Order order = null;
        try (Session session = openSession()) {
            order = session.get(Order.class, orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return order;
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
            for (RecipeToRawMaterial recipeToRawMaterial : materials) {
                RecipeToRawMaterial material;
                material = recipeToRawMaterial;
                material.setRecipe(recipe);
                material.setRawMaterial(material.getRawMaterial());
                material.setQuantity(material.getQuantity());
                material.setUnit(material.getUnit());
                session.merge(material);
            }
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recipe;
    }
    public Order addOrder(List<Product> products, String tableName, DiningOption diningOption) {
        if (tableManager.isTableBusy(diningOption, tableName)) {
            System.out.println("Table is busy. Cannot place a new order.");
            return null; // or throw an exception
        }

        Order order = new Order();
        order.setTableName(tableName);
        order.setDiningOption(diningOption);
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(OrderStatus.PLACED);

        // Calculate total price
        double totalPrice = products.stream().mapToDouble(Product::getPrice).sum();
        order.setTotalPrice(totalPrice);

        // Set products to the order
        order.setProducts(products);

        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(order);
            transaction.commit();

            // Mark the table as busy
            tableManager.setTableBusy(diningOption, tableName, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return order;
    }

    public void payOrder(Order order) {
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();

            // Update order status to PAID
            order.setStatus(OrderStatus.PAID);
            session.update(order);
            transaction.commit();

            // Mark the table as free
            tableManager.setTableBusy(order.getDiningOption(), order.getTableName(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateProduct(Product product) {
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            if (product != null) {
                session.merge(product);
            }

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateRawMaterial(RawMaterial material) {
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            if (material != null) {
                session.merge(material);
            }

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateRecipe(String newRecipeName, Product newProduct, List<RecipeToRawMaterial> newRecipeToRawMaterials) {
        try(Session session = openSession()){
            Transaction transaction = session.beginTransaction();
            Recipe recipe = getRecipeByName(newRecipeName);
            if (recipe != null) {
                // Update the Recipe's fields
                recipe.setName(newRecipeName);
                recipe.setProduct(newProduct);

                // Clear existing raw materials and set the new list
               List<RecipeToRawMaterial> materials =  recipe.getMaterials();
               for (RecipeToRawMaterial material: materials) {
                   session.remove(material);
               }

               for (RecipeToRawMaterial material: newRecipeToRawMaterials) {
                   session.merge(material);
               }
                session.merge(recipe);
            }

            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void deleteProduct(int productId){
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            Product product = session.get(Product.class,productId);

            if (product != null) {
                session.remove(product);
            }

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteRawMaterial(int rawMaterialId) {
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            RawMaterial material = session.get(RawMaterial.class,rawMaterialId);

            if (material != null) {
                session.remove(material);
            }

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteRecipe(int recipeId) {
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            Recipe recipe = session.get(Recipe.class,recipeId);

            if (recipe != null) {
                for (RecipeToRawMaterial rtm: recipe.getMaterials()){
                    session.remove(rtm);
                }
                session.remove(recipe);
            }

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }



}
