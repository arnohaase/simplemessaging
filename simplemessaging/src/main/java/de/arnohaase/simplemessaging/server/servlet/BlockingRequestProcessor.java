package de.arnohaase.simplemessaging.server.servlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.arnohaase.simplemessaging.common.Message;
import de.arnohaase.simplemessaging.common.MessageCategory;
import de.arnohaase.simplemessaging.common.serialize.MessageSerializer;
import de.arnohaase.simplemessaging.server.messaging.Messaging;


/**
 * This request processor also works with Servlet API 2.x. However it blocks while waiting for messages, increasing the footprint
 *  by one thread per client.
 * 
 * @author arno
 */
abstract class BlockingRequestProcessor extends AbstractRequestProcessor {
    private final HttpServletResponse _response;
    private final Object _lock = new Object ();
    private List<Message> _messages;
    
    
    public BlockingRequestProcessor (Messaging messaging, HttpServletRequest request, HttpServletResponse response, Collection<MessageCategory> relevantCategories) {
        super (messaging, request, relevantCategories);
        _response = response;
    }

    @Override
    protected void init () {
    }

    @Override
    public void consume (List<Message> messages) {
        synchronized (_lock) {
            _messages = messages;
            _lock.notify ();
        }
    }

    @Override
    protected void afterRegistration () throws Exception {
        synchronized (_lock) {
            if (_messages == null) { 
                try {
                    _lock.wait (_messaging.getConfig ().getWaitForMessagesSeconds () * 1000);
                }
                catch (InterruptedException exc) {}
            }

            if (_messages == null)
                _messages = new ArrayList<Message> ();

            new MessageSerializer ().serialize (_response.getOutputStream (), _messages);
            _response.getOutputStream ().flush ();
        }
    }

    @Override
    protected void onSynchronousError () {
    }
}



