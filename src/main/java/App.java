import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class App {
    public static void main(String[] args) {
        Alien alien = null;

        // Create a configuration instance and load the configuration file
        Configuration configuration = new Configuration().configure().addAnnotatedClass(Alien.class);

        // Build a ServiceRegistry using the Configuration
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        // Build a SessionFactory using the ServiceRegistry
        SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);

        // Open a new session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        Transaction transaction = session.beginTransaction();

        // Commit the transaction
        transaction.commit();

        alien = session.get(Alien.class,1);

        System.out.println(alien);

        // Close the session
        session.close();

        // Close the SessionFactory
        sessionFactory.close();
    }
}
