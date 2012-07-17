package de.arnohaase.simplemessaging.server.messaging.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import de.arnohaase.simplemessaging.common.Message;
import de.arnohaase.simplemessaging.common.MessageCategory;
import de.arnohaase.simplemessaging.common.util.SecuredExecutor;
import de.arnohaase.simplemessaging.common.util.ThrowingRunnable;
import de.arnohaase.simplemessaging.server.messaging.config.MessageStore;
import de.arnohaase.simplemessaging.server.messaging.config.MessagingConfig;
import de.arnohaase.simplemessaging.server.messaging.iface.MessageConsumer;


public class MessagingImpl {
    private final MessagingConfig _config ;
    private final MessageStore _messageStore;
    private final Map <MessageConsumer, Date> _consumers = Collections.synchronizedMap (new HashMap <MessageConsumer, Date> ());
    private final ExecutorService _notificationThreadpool = Executors.newSingleThreadExecutor ();

    
    public MessagingImpl (MessagingConfig config) throws Exception {
        _config = config;
        _messageStore = config.getMessageStore ();
    }
    
    
    public void heartbeat () throws Exception {
        final Date cutoffTimestamp = (_config.getMessageCachingMillis () > 0) ? new Date (System.currentTimeMillis () - _config.getMessageCachingMillis ()) : null;
        
        _messageStore.removeOutdatedMessages (cutoffTimestamp, _config.getMaxNumMessages ());
    }
    
    public long getNextMessageNumber () throws Exception {
        return _messageStore.getNextMessageNumber ();
    }

    public long getMsgNumberForTimestamp (Date timestamp) throws Exception {
        return _messageStore.getMsgNumberForTimestamp (timestamp);
    }

    public void registerConsumer (MessageConsumer consumer) throws Exception {
        _consumers.put (consumer, new Date ());
        sendAndCloseIfMessages (consumer);
    }

    private void sendAndCloseIfMessages (MessageConsumer consumer) throws Exception {
        // Removal servers thread safety: the consumer is not registered while it is being processed, so concurrent calls ignore it. At the same
        //  time this serves as a safety net: If it is no longer registered, e.g. due to a timeout in the servlet, we can safely ignore it here.
        final Date registeredDate = _consumers.remove (consumer); 
        if (registeredDate == null)
            return;

        // GC for registered consumers - since it is removed from the collection of registered consumers, we can safely abort here
        if (registeredDate.before (new Date (System.currentTimeMillis () - _config.getWaitForMessagesSeconds () * 1000)))
            return;
        
        final List<Message> messages = getMessagesFor (consumer);
        if (messages.isEmpty ()) {
            // the consumer is still valid but there were no messages --> register it again
            _consumers.put (consumer, registeredDate);
            return;
        }

        // there were messages --> consume them. The consumer is not registered any more, so no cleanup is required
        consumer.consume (messages);
    }
    
    
    /**
     * This message notifies Messaging that a different Messaging instance (i.e. in a cluster) added messages to the MessageStore.
     */
    public void newMessagesHaveArrived () {
        notifyConsumersInBackground (null);
    }
    
    private List <Message> getMessagesFor (MessageConsumer consumer) throws Exception {
        final List<Message> result = new ArrayList<Message> ();
        
        for (Message msg: _messageStore.getMessages (consumer.getMinInterestingMessageNumber (), consumer.getRelevantCategories ()))
            if (consumer.shouldReceive (msg))
                result.add (msg);
        
        return result;
    }
    
    private Collection<MessageConsumer> getConsumersForCategory (MessageCategory category) {
        final List<MessageConsumer> result = new ArrayList<MessageConsumer> ();

        if (category == null)
            result.addAll (_consumers.keySet ());
        else 
            addRelevantConsumers (category, result);

        return result;
    }

    private void addRelevantConsumers (MessageCategory category, final List<MessageConsumer> result) {
        for (MessageConsumer consumer: _consumers.keySet ())
            if (consumer.getRelevantCategories () == null || consumer.getRelevantCategories ().contains (category))
                result.add (consumer);
    }
    
    public void publishMessage (final MessageCategory category, String categoryDetails, Object msgData) throws Exception {
        _messageStore.addMessage (category, categoryDetails, msgData, new Runnable () {
            public void run () {
                // perform checks and notifications of consumers in a different thread: mainly decoupling from the calling thread. 
                //  NB: There is only one background thread to avoid race conditions
                notifyConsumersInBackground (category);
                _config.getClusterNotifier ().notifyAllMessagingInstances ();
            }
        });
    }
    
    private void notifyConsumersInBackground (final MessageCategory category) {
        _notificationThreadpool.execute (new Runnable () {
            public void run () {
                // retrieve the consumers that are *now* current in the notification thread --> avoid race conditions with addMessage
                for (MessageConsumer consumer: getConsumersForCategory (category)) {
                    notifyConsumer (consumer);
                }
            }
        });
    }
    
    private void notifyConsumer (final MessageConsumer consumer) {
        new SecuredExecutor (_config.getLogger ()).doSecured (new ThrowingRunnable () {
            public void run () throws Exception {
                sendAndCloseIfMessages (consumer);
            }  
        });
    }
    
    public void shutdown () {
        _consumers.clear ();
        _messageStore.shutdown ();
        _notificationThreadpool.shutdown ();
        _config.getClusterNotifier ().shutdown ();
    }
}
