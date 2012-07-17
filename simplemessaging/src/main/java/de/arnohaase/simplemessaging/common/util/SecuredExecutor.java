package de.arnohaase.simplemessaging.common.util;

import de.arnohaase.simplemessaging.server.messaging.config.MessagingLogger;


public class SecuredExecutor {
    private final MessagingLogger _log;
    
    public SecuredExecutor (MessagingLogger log) {
        _log = log;
    }

    public void doSecured (ThrowingRunnable r) {
        try {
            r.run ();
        }
        catch (Exception exc) {
            try {
                _log.logException (exc);
            }
            catch (Exception e) {
                exc.printStackTrace ();
                e.printStackTrace (); // an exception occurred while logging an exception - this is a last resort to attempt to at least save the stacktrace
            }
        }
    }
}
