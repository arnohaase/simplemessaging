package de.arnohaase.simplemessaging.server.messaging.config;


public class MessagingConfigImpl implements MessagingConfig {
    private final MessageStore _messageStore;
    private final MessagingClusterNotifier _clusterNotifier;
    private final long _heartbeatSeconds;
    private final MessagingLogger _logger;
    private final int _maxNumMessages;
    private final int _messageCachingMillis;
    private final long _waitForMessagesSeconds;

    public MessagingConfigImpl (MessageStore messageStore, MessagingClusterNotifier clusterNotifier, long heartbeatSeconds, MessagingLogger logger, int maxNumMessages,
            int messageCachingMillis, long waitForMessagesSeconds) {
        _messageStore = messageStore;
        _clusterNotifier = clusterNotifier;
        _heartbeatSeconds = heartbeatSeconds;
        _logger = logger;
        _maxNumMessages = maxNumMessages;
        _messageCachingMillis = messageCachingMillis;
        _waitForMessagesSeconds = waitForMessagesSeconds;
    }

    public MessageStore getMessageStore () {
        return _messageStore;
    }
    public MessagingClusterNotifier getClusterNotifier () {
        return _clusterNotifier;
    }
    public long getHeartbeatSeconds () {
        return _heartbeatSeconds;
    }
    public MessagingLogger getLogger () {
        return _logger;
    }
    public int getMaxNumMessages () {
        return _maxNumMessages;
    }
    public int getMessageCachingMillis () {
        return _messageCachingMillis;
    }
    public long getWaitForMessagesSeconds () {
        return _waitForMessagesSeconds;
    }
}
