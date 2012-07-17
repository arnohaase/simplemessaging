package de.arnohaase.simplemessaging.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.arnohaase.simplemessaging.server.messaging.Messaging;
import de.arnohaase.simplemessaging.server.messaging.MessagingHolder;



/**
 * This is a simple servlet-based implementation for clustering SimpleMessaging. It accepts
 *  notifications of other nodes that new messages were provided. It collaborates with
 *  UrlBasedMessagingClusterNotifier.<br>
 *  
 * This class is irrelevant for non-clustered use.
 * 
 * @author arno
 */
public class ClusterMessagingNotificationServlet extends HttpServlet {
    /**
     * Override this if access to the Messaging instance is *not* through MessagingHolder.
     */
    protected Messaging getMessaging () {
        return MessagingHolder.getMessaging ();
    }

    @Override
    protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("cluster notification at" + req.getRequestURI());
        getMessaging ().newMessagesHaveArrived ();
    }
}
