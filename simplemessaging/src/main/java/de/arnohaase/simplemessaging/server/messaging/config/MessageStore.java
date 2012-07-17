package de.arnohaase.simplemessaging.server.messaging.config;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import de.arnohaase.simplemessaging.common.Message;
import de.arnohaase.simplemessaging.common.MessageCategory;


/**
 * A MessageStore is responsible for the physical storage of messages. Implementations must be thread safe.
 * 
 * @author arno
 */
public interface MessageStore {
    long INITIAL_MESSAGE_NUMBER = 1;
    
    void addMessage (MessageCategory category, String categoryDetails, Object msgData, Runnable publish) throws Exception;
    void removeOutdatedMessages (Date cutOffTimestamp, int maxNumMessages) throws Exception;

    long getNextMessageNumber () throws Exception;
    long getMsgNumberForTimestamp (Date timestamp) throws Exception;
    List <Message> getMessages (long minInterestingMessageNumber, Collection <MessageCategory> categories) throws Exception;
   
    void shutdown ();
}
