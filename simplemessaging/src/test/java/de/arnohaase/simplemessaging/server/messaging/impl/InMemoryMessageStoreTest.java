package de.arnohaase.simplemessaging.server.messaging.impl;

import de.arnohaase.simplemessaging.server.messaging.config.MessageStore;
import de.arnohaase.simplemessaging.server.messaging.impl.InMemoryMessageStore;


public class InMemoryMessageStoreTest extends AbstractMessageStoreTest {
    @Override
    public MessageStore createMessageStore () throws Exception {
        return new InMemoryMessageStore ();
    }
}
