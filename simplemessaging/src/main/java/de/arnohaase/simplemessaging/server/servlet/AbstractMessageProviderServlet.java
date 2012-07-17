package de.arnohaase.simplemessaging.server.servlet;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import de.arnohaase.simplemessaging.common.MessagingConstants;
import de.arnohaase.simplemessaging.server.messaging.Messaging;
import de.arnohaase.simplemessaging.server.messaging.MessagingHolder;


public abstract class AbstractMessageProviderServlet extends HttpServlet {
    public abstract AbstractRequestProcessor createRequestProcessor (HttpServletRequest request, HttpServletResponse response);

    
    protected Messaging getMessaging () {
        return MessagingHolder.getMessaging ();
    }
    
    private Long parse (String s) {
        if (s == null)
            return null;
        
        try {
            return new Long (s);
        }
        catch (Exception exc) {
            return null;
        }
    }
    
    protected long getMinMsgNumber (HttpServletRequest request) {
        final Long minMsgNumber     = parse (request.getParameter (MessagingConstants.MIN_MSG_NUMBER_PARAM_NAME));
        final Long startingAtMillis = parse (request.getParameter (MessagingConstants.STARTING_AT_TIMESTAMP_MILLIS));

        if (minMsgNumber != null)
            return minMsgNumber > 0 ? minMsgNumber : minMsgNumber + getMessaging ().getNextMessageNumber ();

        if (startingAtMillis != null)
            return getMessaging ().getMsgNumberForTimestamp (new Date (startingAtMillis));

        return 0;
    }
    
    @Override
    protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final AbstractRequestProcessor processor = createRequestProcessor (req, resp);

        try {
            processor.init ();
            getMessaging ().registerConsumer (processor);

            processor.afterRegistration ();
        }
        catch (IOException exc) {
            processor.onSynchronousError ();
            throw exc;
        }
        catch (RuntimeException exc) {
            processor.onSynchronousError ();
            throw exc;
        }
        catch (Exception exc) {
            processor.onSynchronousError ();
            throw new ServletException (exc);
        }
        catch (Error exc) {
            processor.onSynchronousError ();
            throw exc;
        }
    }
}
