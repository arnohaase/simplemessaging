package de.arnohaase.simplemessaging.client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import de.arnohaase.simplemessaging.common.Message;


public class SimpleMessagingClient {
    private final int _readerShutdownTimeout;
    private final int _callbackShutdownTimeout;
    
    private final MessagingClientLogger _logger;
    private final ScheduledExecutorService _readerThreadPool = Executors.newScheduledThreadPool (1);
    private final ExecutorService _callbackThreadPool;
    private final MessageListener _messageListener;
    
    private final BlockingQueue <Message> _messages;
    
    private final HttpMessagePoller _poller;
    
    public static SimpleMessagingClient createForPolling (String serverUrl, MessagingClientConfiguration config) {
        return new SimpleMessagingClient (serverUrl, null, config);
    }

    public static SimpleMessagingClient createForCallback (String serverUrl, MessagingClientConfiguration config, MessageListener callback) {
        return new SimpleMessagingClient (serverUrl, callback, config);
    }
    
    private SimpleMessagingClient (String serverUrl, MessageListener l, MessagingClientConfiguration config) {
        _readerShutdownTimeout = config.getReaderShutdownWaitMillis ();
        _callbackShutdownTimeout = config.getCallbackShutdownWaitMillis ();
        
        _logger = SecuredLoggerFactory.createSecured (config.getLogger ());
        
        _messageListener = l;
        _callbackThreadPool = createCallbackThreadpool (l, config);
        _messages = new ArrayBlockingQueue <Message> (config.getMaxClientSideQueueSize ()); 
        
        _poller = new HttpMessagePoller (serverUrl, _logger, config.getHeaderProvider (), 
                config.getSocketTimeoutMillis (), config.getStartingAtTimestamp (), config.getStartingAtMessageNumber (),
                config.getFailureIntervalBaseMillis (), config.getFailureIntervalFactor (), config.getFailureIntervalMaxMillis ());
        
        initReaderThread (config);
    }
    
    private static ExecutorService createCallbackThreadpool (MessageListener l, MessagingClientConfiguration config) {
        if (l == null)
            return null;
        
        switch (config.getAnzCallbackThreads ()) {
            case 0:  return null;
            case 1:  return Executors.newSingleThreadExecutor ();
            default: return Executors.newFixedThreadPool (config.getAnzCallbackThreads ());
        }
    }

    private void initReaderThread (MessagingClientConfiguration config) {
        final Runnable r = new Runnable () {
            public void run () {
                for (Message message: _poller.readFromServer ()) {
                    handleNewMessage (message); 
                }
            }
        };

        final int delay = config.getDelayBetweenPollingMillis () <= 0 ? 1 : config.getDelayBetweenPollingMillis ();
        _readerThreadPool.scheduleWithFixedDelay (r, 0, delay, TimeUnit.MILLISECONDS);
    }
    
    public boolean hasCallback () {
        return _callbackThreadPool != null;
    }
    
    private void handleNewMessage (final Message message) {
        if (hasCallback ())
            _callbackThreadPool.submit (new Runnable () {
                public void run () {
                    _messageListener.onMessage (message);
                }
            });
        else {
            while (!_messages.offer (message)) {
                if (_messages.poll () != null)
                    _logger.logMessageDiscarded ();
            }
        }
    }
    
    /**
     * returns (and deletes) the next message, waiting until such a message is available
     */
    public Message blockingGet () {
        checkNoCallback ();
        
        try {
            return _messages.take ();
        } catch (InterruptedException exc) {
            _logger.logInterruptedException (exc);
            return null;
        }
    }

    /**
     * returns (and deletes) the next message, waiting until such a message is available or a timeout of timeoutMillis milliseconds is reached
     */
    public Message blockingGet (long timeoutMillis) {
        checkNoCallback ();
        
        try {
            return _messages.poll (timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException exc) {
            _logger.logInterruptedException (exc);
            return null;
        }
    }
    
    private void checkNoCallback () {
        if (hasCallback ())
            throw new IllegalStateException ("This message consumer is read by callback - active polling is therefore not permitted.");
    }
    
    /**
     * returns (and deletes) the next message if one is available, or <code>null</code> if there is currently no message.
     */
    public Message poll () {
        checkNoCallback ();
        return _messages.poll ();
    }
    
    /**
     * returns the next message wihtout deleting it, or <code>null</code> if there is currently no message.
     */
    public Message peek () {
        checkNoCallback ();
        return _messages.peek ();
    }

    public void shutdown () {
        try {
            _readerThreadPool.awaitTermination (_readerShutdownTimeout, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException exc) {
            _logger.logInterruptedException (exc);
        }

        if (_callbackThreadPool != null) {
            try {
                _callbackThreadPool.awaitTermination (_callbackShutdownTimeout, TimeUnit.MILLISECONDS); 
            }
            catch (InterruptedException exc) {
                _logger.logInterruptedException (exc);
            }
        }

        _poller.shutdown ();
        _messages.clear ();
    }
}









