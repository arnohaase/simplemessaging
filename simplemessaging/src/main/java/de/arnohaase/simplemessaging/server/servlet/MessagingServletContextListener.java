package de.arnohaase.simplemessaging.server.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import de.arnohaase.simplemessaging.server.messaging.MessagingHolder;



/**
 * This listener shuts down Messaging when the application is shut down - at least if
 *  MessagingHolder is used.<br>
 *  
 * If MessagingHolder is not used, applications need to call Messaging.shutdown() during application
 *  shutdown in some other way.
 * 
 * @author arno
 */
public class MessagingServletContextListener implements ServletContextListener {
    public void contextInitialized (ServletContextEvent sce) {
    }

    public void contextDestroyed (ServletContextEvent sce) {
        // this is safe to call even if MessagingHolder is not used, in which case it does nothing
        MessagingHolder.shutdown ();
    }
}
