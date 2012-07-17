package de.arnohaase.simplemessaging.server.messaging.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;


public interface MessagingLogger {
    void logException (Exception exc);
    void logHeartbeat ();
    void logShuttingDown ();
    void logFinishedShutdown ();
    
    void logClusterNotificationFailure (String url, int httpResponse, String httpBody, Exception exc);
    
    MessagingLogger STDOUT_LOGGER = new MessagingLogger () {
        public void logException (Exception exc) {
            exc.printStackTrace ();
        }
        
        public void logHeartbeat () {
            System.out.println ("performing Messaging heartbeat");
        }
        
        public void logShuttingDown () {
            System.out.println ("shutting down Messaging");
        }
        
        public void logFinishedShutdown () {
            System.out.println ("finished shutdown Messaging");
        }
        
        public void logClusterNotificationFailure (String url, int httpResponse, String httpBody, Exception exc) {
            if (httpResponse != 0)
                System.out.println ("Failure notifying url " + url + ": HTTP response " + httpResponse + ", response body: " + httpBody);
            else {
                System.err.println ("Failure notifying url " + url + ":");
                exc.printStackTrace ();
            }
        }
    };
    
    MessagingLogger LOG4J_LOGGER = new MessagingLogger () {
        private final Logger _log = Logger.getLogger (MessagingLogger.class);
        
        public void logException (Exception exc) {
            _log.error (exc);
        }
        
        public void logHeartbeat () {
            _log.info ("performing Messaging heartbeat");
        }
        
        public void logShuttingDown () {
            _log.info ("shutting down Messaging");
        }
        
        public void logFinishedShutdown () {
            _log.info ("finished shutdown Messaging");
        }
        
        public void logClusterNotificationFailure (String url, int httpResponse, String httpBody, Exception exc) {
            if (httpResponse != 0)
                _log.warn ("Failure notifying url " + url + ": HTTP response " + httpResponse + ", response body: " + httpBody);
            else
                _log.warn ("Failure notifying url " + url + ".", exc);
        }
    };
    
    MessagingLogger COMMONS_LOGGER = new MessagingLogger () {
        private final Log _log = LogFactory.getLog (MessagingLogger.class);
        
        public void logException (Exception exc) {
            _log.error (exc);
        }

        public void logHeartbeat () {
            _log.info ("performing Messaging heartbeat");
        }

        public void logShuttingDown () {
            _log.info ("shutting down Messaging");
        }

        public void logFinishedShutdown () {
            _log.info ("finished shutdown Messaging");
        }
        
        public void logClusterNotificationFailure(String url, int httpResponse, String httpBody, Exception exc) {
            if (httpResponse != 0)
                _log.warn ("Failure notifying url " + url + ": HTTP response " + httpResponse + ", response body: " + httpBody);
            else
                _log.warn ("Failure notifying url " + url + ".", exc);
        }
    };
}
