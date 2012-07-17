package de.arnohaase.simplemessaging.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This interface allows registering callbacks for typically interesting events, e.g. for logging them.<br>
 * 
 * The MessageConsumer implementation takes care of the actual handling of these events, being fully functional
 *  without any collaboration from an implementation of this interface.<br>
 *  
 * Implementations must *not* throw an exception from any of the methods.
 * 
 * @author arno
 */
public interface MessagingClientLogger {
    //TODO javadoc
    void logInterruptedException (InterruptedException exc);
    
    void logServerError          (int httpReturnCode, String responseBody);
    void logTransmissionError    (Exception exc);
    
    void logSuccessfulCall (int anzMessages, int httpResponseCode, long callDuration);
    void logFailureWait (int anzFehlgeschlageneCalls, long interval);
    
    void logMessageDiscarded ();
    
    MessagingClientLogger COMMONS_LOGGER = new MessagingClientLogger() {
        private final Log _log = LogFactory.getLog (SimpleMessagingClient.class);
        
        public void logInterruptedException (InterruptedException exc) {
            _log.error ("interrupted", exc);
        }

        public void logServerError (int errorCode, String responseBody) {
            _log.error ("http error: " + errorCode + ", message body: " + responseBody);
        }

        public void logTransmissionError (Exception exc) {
            _log.error ("transmission error", exc);
        }
        
        public void logSuccessfulCall(int anzMessages, int httpResponseCode, long callDuration) {
            _log.debug ("successful polling call. Retrieved " + anzMessages + " messages. Call duration: " + callDuration + "ms, response code: " + httpResponseCode);
        }
        
        public void logFailureWait(int anzFehlgeschlageneCalls, long interval) {
            _log.info ("waiting due to failures: " + anzFehlgeschlageneCalls + " failures, waiting " + interval + "ms.");
        }
        
        public void logMessageDiscarded() {
            _log.warn ("internal message queue was full - the oldest message in the queue was discarded");
        }
    };
}
