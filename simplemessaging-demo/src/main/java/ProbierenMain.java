import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import de.arnohaase.simplemessaging.server.messaging.impl.hibernate.HibernateMessage;
import de.arnohaase.simplemessaging.server.messaging.impl.hibernate.HibernateMessagingLock;


public class ProbierenMain {
    public static void main(String[] args) {
        
        
        final Configuration config = new Configuration ();
        config.addAnnotatedClass (HibernateMessage.class);
        config.addAnnotatedClass (HibernateMessagingLock.class);
        
        config.setProperty ("hibernate.dialect",                 "org.hibernate.dialect.H2Dialect");
        config.setProperty ("hibernate.connection.driver_class", "org.h2.Driver");
        config.setProperty ("hibernate.connection.username",     "sa");
        config.setProperty ("hibernate.connection.password",     "");
//        config.setProperty ("hibernate.connection.url",          "jdbc:h2:mem:ap-test-db");
        config.setProperty ("hibernate.connection.url",          "jdbc:h2:tcp://localhost/~/tmp/ap-test-db");
        
        config.setProperty ("hibernate.hbm2ddl.auto", "update"); // create-drop / create / update / validate
        
        config.setProperty ("hibernate.connection.provider_class", "org.hibernate.connection.C3P0ConnectionProvider");
        config.setProperty ("hibernate.c3p0.max_size", "20");
        config.setProperty ("hibernate.c3p0.min_size", "2");
        
        config.setProperty ("hibernate.show_sql",   "true");

        final SessionFactory sf = config.buildSessionFactory();
    }
}
