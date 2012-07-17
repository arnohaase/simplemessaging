package de.arnohaase.simplemessaging.server.messaging;

import de.arnohaase.simplemessaging.server.messaging.config.MessageStore;
import de.arnohaase.simplemessaging.server.messaging.config.MessagingClusterNotifier;
import de.arnohaase.simplemessaging.server.messaging.config.MessagingConfig;
import de.arnohaase.simplemessaging.server.messaging.config.MessagingConfigImpl;
import de.arnohaase.simplemessaging.server.messaging.config.MessagingLogger;
import de.arnohaase.simplemessaging.server.messaging.impl.InMemoryMessageStore;


/**
 * This is a helper class for building MessagingConfig instances. It provides setter methods while the
 *  actual MessagingConfig object is immutable.
 * 
 * @author arno
 */
public class MessagingConfigBuilder {
    private MessageStore _messageStore = new InMemoryMessageStore ();
    private MessagingClusterNotifier _clusterNotifier = MessagingClusterNotifier.NULL_NOTIFIER;
    private long _heartbeatSeconds = 15 * 60;
    private MessagingLogger _logger = MessagingLogger.LOG4J_LOGGER;
    private int _maxNumMessages = 1000;
    private int _messageCachingMillis = 0;
    private long _waitForMessagesSeconds = 5 * 60;
    
    public MessagingConfigBuilder setMessageStore (MessageStore messageStore) {
        _messageStore = messageStore;
        return this;
    }

    public MessagingConfigBuilder setClusterNotifier (MessagingClusterNotifier clusterNotifier) {
        _clusterNotifier = clusterNotifier;
        return this;
    }

    public MessagingConfigBuilder setHeartbeatSeconds (long heartbeatSeconds) {
        _heartbeatSeconds = heartbeatSeconds;
        return this;
    }

    public MessagingConfigBuilder setLogger (MessagingLogger logger) {
        _logger = logger;
        return this;
    }

    public MessagingConfigBuilder setMaxNumMessages (int maxNumMessages) {
        _maxNumMessages = maxNumMessages;
        return this;
    }

    public MessagingConfigBuilder setMessageCachingMillis (int messageCachingMillis) {
        _messageCachingMillis = messageCachingMillis;
        return this;
    }

    public MessagingConfigBuilder setWaitForMessagesSeconds (long waitForMessagesSeconds) {
        _waitForMessagesSeconds = waitForMessagesSeconds;
        return this;
    }

    public MessagingConfig buildConfig () {
        return new MessagingConfigImpl (_messageStore, _clusterNotifier, _heartbeatSeconds, _logger, _maxNumMessages, _messageCachingMillis, _waitForMessagesSeconds);
    }
}
