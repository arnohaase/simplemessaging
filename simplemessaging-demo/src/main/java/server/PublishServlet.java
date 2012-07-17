package server;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.arnohaase.simplemessaging.common.MessageCategory;
import de.arnohaase.simplemessaging.server.messaging.MessagingHolder;


public class PublishServlet extends HttpServlet {
    @Override
    protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter ().println ("Hi there - publishing message at " + new Date());
        
        MessagingHolder.getMessaging ().publishMessage (new MessageCategory ("abc"), null, "Hallo");
    }
}
