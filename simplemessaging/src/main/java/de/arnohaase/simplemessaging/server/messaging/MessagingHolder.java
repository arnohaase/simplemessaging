package de.arnohaase.simplemessaging.server.messaging;

import de.arnohaase.simplemessaging.server.messaging.config.MessagingConfig;


/**
 * This is a convenience class to hold a reference to the single instance of Messaging for the simple case that only one instance
 *  per JVM / classloader exists.<br>
 *  
 * The <code>init</code> message must be called before the first usage of Messaging. Only the first call to 
 *  <code>init</code> has an effect, subsequent calls are silently ignored.<br>
 *  
 * The MessageProvider servlets retrieve the Messaging instance through their respective <code>getMessaging()</code>
 *  methods. Their default implementations retrieve the instance from this holders, for different behavior (i.e.
 *  different multiplicity in particular) override the respective methods in the servlets.
 * 
 * @author arno
 */
public class MessagingHolder {
    private static volatile Messaging _messaging;
    
    public static synchronized void init (MessagingConfig config) throws Exception {
        if (_messaging != null)
            return;
        
        try {
            _messaging = new Messaging (config);
        }
        catch (Exception exc) {
            config.getLogger ().logException (exc);
            throw exc;
        }
    }

    public static Messaging getMessaging () {
        return _messaging;
    }
    
    public static synchronized void shutdown () {
        if (_messaging == null)
            return;
        
        _messaging.shutdown ();
        _messaging = null;
    }
}
