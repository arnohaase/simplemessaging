package server;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

import de.arnohaase.simplemessaging.common.Message;
import de.arnohaase.simplemessaging.common.MessageCategory;
import de.arnohaase.simplemessaging.server.messaging.MessagingConfigBuilder;
import de.arnohaase.simplemessaging.server.messaging.MessagingHolder;
import de.arnohaase.simplemessaging.server.messaging.config.MessagingConfig;
import de.arnohaase.simplemessaging.server.messaging.config.MessagingLogger;
import de.arnohaase.simplemessaging.server.messaging.impl.UrlBasedMessagingClusterNotifier;
import de.arnohaase.simplemessaging.server.messaging.impl.hibernate.HibernateMessage;
import de.arnohaase.simplemessaging.server.messaging.impl.hibernate.HibernateMessageStore;
import de.arnohaase.simplemessaging.server.messaging.impl.hibernate.HibernateMessagingLock;
import de.arnohaase.simplemessaging.server.servlet.AbstractMessageProviderServlet_2_x;


public class SyncPollingServlet extends AbstractMessageProviderServlet_2_x {
    @Override
    public void init () throws ServletException {
        try { 
//            MessagingHolder.init (createSimpleConfig ());
            MessagingHolder.init (createClusterConfig ());
        }
        catch (Exception exc) {
            exc.printStackTrace ();
            throw new ServletException (exc);
        }
    }
    
    @SuppressWarnings ("unused")
    private MessagingConfig createSimpleConfig () {
        return new MessagingConfigBuilder ().buildConfig ();
    }

    private MessagingConfig createClusterConfig () throws Exception {
        return new MessagingConfigBuilder ()
        .setMessageStore (new HibernateMessageStore (_sf))
        .setClusterNotifier (new UrlBasedMessagingClusterNotifier (Arrays.asList (
                "http://localhost:8080/ap1/clustersync", 
                "http://localhost:8080/ap2/clustersync"
        ), 4, MessagingLogger.STDOUT_LOGGER))
        .buildConfig ();
    }
    
    @Override
    protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet (req, resp);
    }
    
    @Override
    protected Collection<MessageCategory> getRelevantCategories (HttpServletRequest request) {
        return null;
    }

    @Override
    protected boolean shouldReceive (HttpServletRequest request, Message msg) {
        return true;
    }

    
    private static final SessionFactory _sf = createConfiguration ().buildSessionFactory ();

    /**
     * In real application code this configuration should of course come from a DataSource - this is *not* production
     *  grade code!
     */
    private static Configuration createConfiguration () {
        final AnnotationConfiguration config = new AnnotationConfiguration ();
        config.addAnnotatedClass (HibernateMessage.class);
        config.addAnnotatedClass (HibernateMessagingLock.class);
        
        config.setProperty ("hibernate.dialect",                 "org.hibernate.dialect.H2Dialect");
        config.setProperty ("hibernate.connection.driver_class", "org.h2.Driver");
        config.setProperty ("hibernate.connection.username",     "sa");
        config.setProperty ("hibernate.connection.password",     "");
        config.setProperty ("hibernate.connection.url",          "jdbc:h2:tcp://localhost/~/tmp/ap-test-db");
        
        config.setProperty ("hibernate.hbm2ddl.auto", "update"); // create-drop / create / update / validate
        
        config.setProperty ("hibernate.connection.provider_class", "org.hibernate.connection.C3P0ConnectionProvider");
        config.setProperty ("hibernate.c3p0.max_size", "20");
        config.setProperty ("hibernate.c3p0.min_size", "2");
        
        config.setProperty ("hibernate.show_sql",   "true");

        return config;
    }

}
