package de.arnohaase.simplemessaging.server.messaging.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


import de.arnohaase.simplemessaging.common.Message;
import de.arnohaase.simplemessaging.common.MessageCategory;
import de.arnohaase.simplemessaging.common.SimpleMessage;
import de.arnohaase.simplemessaging.server.messaging.config.MessageStore;


//TODO finer grained locking
public class InMemoryMessageStore implements MessageStore {
    private final Deque <Message> _messages = new LinkedList <Message> ();
    private final Deque <Long>    _timestamps = new LinkedList <Long> ();

    private long _numberOfFirstMessage = INITIAL_MESSAGE_NUMBER;

    
    @Override
    public synchronized void addMessage (MessageCategory category, String categoryDetails, Object msgData, Runnable publish) {
        _messages.addLast (new SimpleMessage (getNextMessageNumber (), category, categoryDetails, msgData));
        _timestamps.addLast (System.currentTimeMillis ());
        publish.run ();
    }
    
    @Override
    public synchronized long getNextMessageNumber () {
        return _numberOfFirstMessage + _messages.size ();
    }
    
    
    @Override
    public synchronized long getMsgNumberForTimestamp (Date timestamp) {
        final Iterator <Message> msgIter = _messages.iterator (); //TODO more efficient algorithm?
        final Iterator <Long>    tsIter  = _timestamps.iterator ();
        
        while (tsIter.hasNext ()) {
            final Message msg = msgIter.next ();
            final Long ts = tsIter.next ();
            
            if (ts >= timestamp.getTime ())
                return msg.getSeqNumber ();
        }
        
        return getNextMessageNumber ();
    }
    
    @Override
    public synchronized List <Message> getMessages (long minMsgNumberRaw, Collection<MessageCategory> categories) {
        final List<Message> result = new ArrayList<Message> ();

        int index = -1;
        for (Message msg: _messages) { //TODO more efficient algorithm based on min number
            index++;
            
            final long curMsgNumber = _numberOfFirstMessage + index;
            
            if (curMsgNumber >= minMsgNumberRaw && matches (msg, categories))
                result.add (msg);
        }
        
        return result;
    }

    private boolean matches (Message msg, Collection <MessageCategory> categories) {
        if (categories == null)
            return true;
        
        return categories.contains (msg.getCategory ());
    }
    
    private void removeOldestMessage () {
        _timestamps.removeFirst ();
        _messages.removeFirst ();
        _numberOfFirstMessage++;
    }
    
    @Override
    public synchronized void removeOutdatedMessages (Date cutOffTimestamp, int maxNumMessages) {
        if (cutOffTimestamp != null)
            removeByTimestamp (cutOffTimestamp);

        if (maxNumMessages > 0)
            removeByMsgCount (maxNumMessages);
    }

    private void removeByMsgCount (int maxNumMessages) {
        while (_messages.size () > maxNumMessages)
            removeOldestMessage ();
    }

    private void removeByTimestamp (Date cutOffTimestamp) {
        while (! _timestamps.isEmpty () && _timestamps.getFirst () < cutOffTimestamp.getTime ()) 
            removeOldestMessage ();
    }
    
    
    @Override
    public synchronized void shutdown () {
        _messages.clear ();
        _timestamps.clear ();
    }
}
