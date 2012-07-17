package de.arnohaase.simplemessaging.server.messaging;


public class MessagingException extends RuntimeException {
    public MessagingException (Exception exc) {
        super (exc);
    }
}
