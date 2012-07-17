package de.arnohaase.simplemessaging.server.servlet;

import java.util.Collection;
import java.util.List;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;

import de.arnohaase.simplemessaging.common.Message;
import de.arnohaase.simplemessaging.common.MessageCategory;
import de.arnohaase.simplemessaging.common.serialize.MessageSerializer;
import de.arnohaase.simplemessaging.server.messaging.Messaging;


/**
 * This RequestProcessor uses Servlet API 3.0, detaching thread from socket whild waiting and significantly reducing
 *  the footprint.
 * 
 * @author arno
 */
abstract class DetachedRequestProcessor extends AbstractRequestProcessor {
    private final Object _lock = new Object ();
    private AsyncContext _async;
    
    public DetachedRequestProcessor (Messaging messaging, HttpServletRequest request, Collection<MessageCategory> relevantCategories) {
        super (messaging, request, relevantCategories);
    }

    @Override
    protected void init () {
        synchronized (_lock) {
            _async = _request.startAsync ();
            _async.setTimeout (_messaging.getConfig ().getWaitForMessagesSeconds () * 1000);
        }
    }

    @Override
    public void consume (List<Message> messages) throws Exception {
        synchronized (_lock) {
            try {
                new MessageSerializer ().serialize (_async.getResponse ().getOutputStream (), messages);
            } finally {
                _async.complete ();
            }
        }
    }

    @Override
    protected void afterRegistration () {
    }
    
    @Override
    protected void onSynchronousError () {
        synchronized (_lock) {
            _async.complete ();
        }
    }
}



