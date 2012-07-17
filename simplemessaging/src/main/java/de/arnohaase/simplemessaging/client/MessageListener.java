package de.arnohaase.simplemessaging.client;

import de.arnohaase.simplemessaging.common.Message;


public interface MessageListener {
    void onMessage (Message m);
}
