package de.arnohaase.simplemessaging.server.messaging.config;


/**
 * This interface is relevant only if there is more than one messaging instance (i.e. in a cluster). It
 *  takes care of notifying all messaging instances when a message was published in one of them.<br>
 *  
 * It is the application's responsibility to provide an appropriate implementation and initialize it
 *  correctly (e.g. with all cluster nodes' URLs). The impl subpackage contains some potentially
 *  useful implementations.<br>
 * 
 * The NULL_NOTIFIER is appropriate for non-clustered opration.
 * 
 * @author arno
 */
public interface MessagingClusterNotifier {
    void notifyAllMessagingInstances ();
    void shutdown ();

    MessagingClusterNotifier NULL_NOTIFIER = new MessagingClusterNotifier() {
        public void notifyAllMessagingInstances () {
        }

        public void shutdown() {
        }
    };
}
