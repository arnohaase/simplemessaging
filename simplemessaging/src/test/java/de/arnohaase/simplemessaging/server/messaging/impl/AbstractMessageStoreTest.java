package de.arnohaase.simplemessaging.server.messaging.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import de.arnohaase.simplemessaging.common.Message;
import de.arnohaase.simplemessaging.common.MessageCategory;
import de.arnohaase.simplemessaging.server.messaging.config.MessageStore;


public abstract class AbstractMessageStoreTest extends TestCase {
    protected static final Runnable NULL_RUNNABLE = new Runnable () {
        public void run() {}
    };

    protected static MessageCategory CAT_A = new MessageCategory ("a");
    protected static MessageCategory CAT_B = new MessageCategory ("b");

    public abstract MessageStore createMessageStore () throws Exception;

    public void testAddGetMessages () throws Exception {
        final MessageStore msgStore = createMessageStore ();
        try {
            assertEquals (MessageStore.INITIAL_MESSAGE_NUMBER, msgStore.getNextMessageNumber ());
            assertEquals (0, msgStore.getMessages (0, null).size ());

            final long start = System.currentTimeMillis ();
            msgStore.addMessage (CAT_A, "details-1", "data-1", NULL_RUNNABLE);
            Thread.sleep (100);
            msgStore.addMessage (CAT_A, "details-2", "data-2", NULL_RUNNABLE);
            Thread.sleep (100);
            msgStore.addMessage (CAT_B, "details-3", "data-3", NULL_RUNNABLE);
            Thread.sleep (100);

            assertEquals (MessageStore.INITIAL_MESSAGE_NUMBER + 3, msgStore.getNextMessageNumber ());

            // query by category
            assertEquals (3, msgStore.getMessages (0, null).size ());
            assertEquals (3, msgStore.getMessages (0, Arrays.asList (CAT_A, CAT_B)).size ());
            assertEquals (2, msgStore.getMessages (0, Arrays.asList (CAT_A)).size ());
            assertEquals (1, msgStore.getMessages (0, Arrays.asList (CAT_B)).size ());
            assertEquals (0, msgStore.getMessages (0, Arrays.asList (new MessageCategory ("c"))).size ());

            // query by timestamp
            assertEquals (MessageStore.INITIAL_MESSAGE_NUMBER,     msgStore.getMsgNumberForTimestamp (new Date (start -  50)));
            assertEquals (MessageStore.INITIAL_MESSAGE_NUMBER + 1, msgStore.getMsgNumberForTimestamp (new Date (start +  50)));
            assertEquals (MessageStore.INITIAL_MESSAGE_NUMBER + 2, msgStore.getMsgNumberForTimestamp (new Date (start + 150)));
            assertEquals (MessageStore.INITIAL_MESSAGE_NUMBER + 3, msgStore.getMsgNumberForTimestamp (new Date (start + 250)));
        }
        finally {
            msgStore.shutdown ();
        }
    }

    public void testCallbackOnAddMessage () throws Exception {
        final MessageStore msgStore = createMessageStore ();
        try {
            for (int i=0; i<10; i++)
                msgStore.addMessage (CAT_A, "details-" + i, "data-" + i, NULL_RUNNABLE);
            
            msgStore.addMessage (CAT_A, "details", "msgData", new Runnable () {
                public void run () {
                    try {
                        // ensure that the newly added message is visible in the callback
                        assertEquals (11, msgStore.getMessages (0, null).size ());
                    }
                    catch (Exception exc) {
                        exc.printStackTrace ();
                        fail (exc.toString ());
                    }
                }
            });
        }
        finally {
            msgStore.shutdown ();
        }
    }

    public void testRemoveOutdatedMessagesByNumber () throws Exception {
        final MessageStore msgStore = createMessageStore ();
        try {
            for (int i=0; i<50; i++)
                msgStore.addMessage (CAT_A, "details-" + i, "data-" + i, NULL_RUNNABLE);
            
            assertEquals (50, msgStore.getMessages (0, null).size ());
            
            msgStore.removeOutdatedMessages (null, 20);

            final List<Message> messages = msgStore.getMessages (0, null);
            assertEquals (20, messages.size ());
            
            for (int i=0; i<20; i++)
                assertEquals (i+31, messages.get (i).getSeqNumber ());
        }
        finally {
            msgStore.shutdown ();
        }
    }

    public void testRemoveOutdatedMessagesByTimestamp () throws Exception {
        final MessageStore msgStore = createMessageStore ();
        try {
            for (int i=0; i<20; i++)
                msgStore.addMessage (CAT_A, "details-" + i, "data-" + i, NULL_RUNNABLE);
            
            Thread.sleep (100);
            final Date cutoff = new Date ();
            Thread.sleep (100);
            
            for (int i=0; i<20; i++)
                msgStore.addMessage (CAT_A, "details-" + i, "data-" + i, NULL_RUNNABLE);

            assertEquals (40, msgStore.getMessages (0, null).size ());
            
            msgStore.removeOutdatedMessages (cutoff, 0);
            
            final List<Message> messages = msgStore.getMessages (0, null);
            assertEquals (20, messages.size ());
            
            for (int i=0; i<20; i++)
                assertEquals (i+21, messages.get (i).getSeqNumber ());

            // ensure that even after a 'complete' GC the next message number is kept
            msgStore.removeOutdatedMessages (new Date (System.currentTimeMillis () + 1000), 0);
            assertEquals (41, msgStore.getNextMessageNumber ());
        }
        finally {
            msgStore.shutdown ();
        }
    }
}


