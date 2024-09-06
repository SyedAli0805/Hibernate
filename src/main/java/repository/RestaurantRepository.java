package repository;

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

    public RestaurantRepository() {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration().configure()
                    .addAnnotatedClass(Product.class)
                    .addAnnotatedClass(Category.class)
                    .addAnnotatedClass(RawMaterial.class)
                    .addAnnotatedClass(Recipe.class)
                    .addAnnotatedClass(RecipeToRawMaterial.class)
                    .addAnnotatedClass(Order.class)
                    .addAnnotatedClass(RestaurantTable.class);

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

    public List<RestaurantTable> getTablesByDiningOption(DiningOption diningOption) {
        List<RestaurantTable> restaurantTables = null;
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            restaurantTables = session.createQuery("from RestaurantTable where diningOption = :option", RestaurantTable.class)
                    .setParameter("option", diningOption)
                    .list();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return restaurantTables;
    }

    public RestaurantTable getTableByName(String name) {
        RestaurantTable restaurantTable = null;
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            restaurantTable = session.createQuery("from RestaurantTable where tableName = :name", RestaurantTable.class)
                    .setParameter("name", name)
                    .uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return restaurantTable;
    }

    public Order getOrderByTable(RestaurantTable table){

        try (Session session = openSession()){
            Transaction transaction = session.beginTransaction();
            Order order = session.createQuery("from Order where restaurantTable = :table",Order.class)
                    .setParameter("id",table)
                    .uniqueResult();
            transaction.commit();
            return order;
        }catch (Exception e){
            e.printStackTrace();
        }
       return null;
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

    public void initializeTable() {
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            // Loop through each dining option
            for (DiningOption option : DiningOption.values()) {
                for (int i = 1; i <= 25; i++) {
                    RestaurantTable restaurantTable = new RestaurantTable();
                    restaurantTable.setTableName(option.name() + "_Table_" + i); // RestaurantTable name like DINE_IN_Table_1
                    restaurantTable.setDiningOption(option);
                    restaurantTable.setBusy(false); // Initialize all tables as free (not busy)
                    session.merge(restaurantTable);
                }
            }
            // Commit the transaction after all tables are saved
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTableBusy(RestaurantTable table) {
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createQuery("update RestaurantTable set isBusy = :isBusy where tableName = :tableName")
                    .setParameter("tableName", table.getTableName())
                    .setParameter("isBusy", table.isBusy())
                    .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public Order addOrder(RestaurantTable restaurantTable, Double totalPrice, OrderStatus orderStatus, List<Product> products) {
        Order order = new Order();
        order.setTable(restaurantTable);
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(orderStatus);
        order.setTotalPrice(totalPrice);
        order.setProducts(products);
        restaurantTable.setBusy(true);
        setTableBusy(restaurantTable);
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(order);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return order;
    }

    public void updateOrder(Order order, RestaurantTable currentTable) {
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            if (order != null && order.getTable().equals(currentTable)) {
                session.merge(order);
            } else {
                assert order != null;
                RestaurantTable table = order.getTable();
                table.setBusy(false);
                setTableBusy(table);
                order.setTable(currentTable);
                currentTable.setBusy(true);
                setTableBusy(currentTable);
                session.merge(order);
            }
            transaction.commit();
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
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            Recipe recipe = getRecipeByName(newRecipeName);
            if (recipe != null) {
                // Update the Recipe's fields
                recipe.setName(newRecipeName);
                recipe.setProduct(newProduct);

                // Clear existing raw materials and set the new list
                List<RecipeToRawMaterial> materials = recipe.getMaterials();
                for (RecipeToRawMaterial material : materials) {
                    session.remove(material);
                }

                for (RecipeToRawMaterial material : newRecipeToRawMaterials) {
                    session.merge(material);
                }
                session.merge(recipe);
            }

            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void deleteProduct(int productId) {
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            Product product = session.get(Product.class, productId);

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
            RawMaterial material = session.get(RawMaterial.class, rawMaterialId);

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
            Recipe recipe = session.get(Recipe.class, recipeId);

            if (recipe != null) {
                for (RecipeToRawMaterial rtm : recipe.getMaterials()) {
                    session.remove(rtm);
                }
                session.remove(recipe);
            }

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelOrder(Order order) {
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            Order orderToCancel = session.get(Order.class, order.getId());
            if (orderToCancel != null) {
                orderToCancel.getTable().setBusy(false);
                setTableBusy(orderToCancel.getTable());
                session.remove(orderToCancel);
            }
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void payOrder(Order order) {
        try (Session session = openSession()) {
            Transaction transaction = session.beginTransaction();
            Order orderToPay = session.get(Order.class, order.getId());
            if (orderToPay != null) {
                orderToPay.getTable().setBusy(false);
                setTableBusy(orderToPay.getTable());
                session.createQuery("update Order set status = :status where id = :id")
                        .setParameter("status", OrderStatus.PAID)
                        .setParameter("id", order.getId())
                        .executeUpdate();
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
