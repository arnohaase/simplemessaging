package de.arnohaase.simplemessaging.server.messaging.config;


public interface MessagingConfig {
    int getMessageCachingMillis ();
    int getMaxNumMessages ();
    long getHeartbeatSeconds ();
    long getWaitForMessagesSeconds ();
    
    MessageStore getMessageStore () throws Exception;
    MessagingLogger getLogger ();
    MessagingClusterNotifier getClusterNotifier ();
}
