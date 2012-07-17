package de.arnohaase.simplemessaging.server.messaging.iface;

import java.util.Collection;
import java.util.List;

import de.arnohaase.simplemessaging.common.Message;
import de.arnohaase.simplemessaging.common.MessageCategory;


/**
 * Once a MessageConsumer is registered, its consume() method is called a single time (with
 *  an arbitrary number of messages). After that it is deregistered automatically. This is
 *  done to avoid race conditions.<br>
 *  
 * NB: This is *not* the client callback but an internal part of the server implementation.
 * 
 * @author arno
 */
public interface MessageConsumer {
    Collection <MessageCategory> getRelevantCategories ();
    
    long getMinInterestingMessageNumber ();
    boolean shouldReceive (Message message);
    void consume (List <Message> messages) throws Exception;
}
