package de.arnohaase.simplemessaging.server.messaging;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import de.arnohaase.simplemessaging.common.MessageCategory;
import de.arnohaase.simplemessaging.common.util.SecuredExecutor;
import de.arnohaase.simplemessaging.common.util.ThrowingRunnable;
import de.arnohaase.simplemessaging.server.messaging.config.MessagingConfig;
import de.arnohaase.simplemessaging.server.messaging.iface.MessageConsumer;
import de.arnohaase.simplemessaging.server.messaging.impl.MessagingImpl;


/**
 * This class takes care of housekeeping and lifecycle of the Messaging itself, delegating the actual work to MessagingImpl.
 * 
 * @author arno
 */
public class Messaging {
    private final MessagingConfig _config;
    private final MessagingImpl _impl;
    
    private final ScheduledExecutorService _threadPool = Executors.newScheduledThreadPool (2); 
    
    public Messaging (MessagingConfig config) throws Exception {
        _config = config;
        _impl = new MessagingImpl (config);
        _threadPool.scheduleWithFixedDelay (new Runnable () {
            public void run () {
                new SecuredExecutor (_config.getLogger ()).doSecured (new ThrowingRunnable () {
                    public void run () {
                        _config.getLogger ().logHeartbeat ();
                    }
                });

                new SecuredExecutor (_config.getLogger ()).doSecured (new ThrowingRunnable () {
                    public void run () throws Exception {
                        _impl.heartbeat ();
                    }
                });
            }
        }, _config.getHeartbeatSeconds (), _config.getHeartbeatSeconds (), TimeUnit.SECONDS); 
    }
    
    /**
     * Diese Methode dient (z.B.) im Cluster-Betrieb dazu, dem Messaging mitzuteilen, dass durch eine andere Messaging-Instanz Nachrichten
     *  in den MessageStore gelangt sind. 
     */
    public void newMessagesHaveArrived () {
        _impl.newMessagesHaveArrived ();
    }
    
    public MessagingConfig getConfig () {
        return _config;
    }
    
    public long getNextMessageNumber () throws MessagingException {
        try {
            return _impl.getNextMessageNumber ();
        } catch (Exception exc) {
            throw new MessagingException (exc);
        }
    }

    public long getMsgNumberForTimestamp (Date timestamp) throws MessagingException {
        try {
            return _impl.getMsgNumberForTimestamp (timestamp);
        } catch (Exception exc) {
            throw new MessagingException (exc);
        }
    }

    public void registerConsumer (MessageConsumer consumer) throws MessagingException {
        try {
            _impl.registerConsumer (consumer);
        } catch (Exception exc) {
            throw new MessagingException (exc);
        }
    }
    
    public void publishMessage (MessageCategory category, String categoryDetails, Object msgData) throws MessagingException {
        try {
            _impl.publishMessage (category, categoryDetails, msgData);
        } catch (Exception exc) {
            throw new MessagingException (exc);
        }
    }
    
    public void shutdown () {
        new SecuredExecutor (_config.getLogger ()).doSecured (new ThrowingRunnable () {
            public void run () {
                _config.getLogger ().logShuttingDown ();
            }
        });

        new SecuredExecutor (_config.getLogger ()).doSecured (new ThrowingRunnable () {
            public void run () throws Exception {
                _threadPool.shutdown ();
                try {
                    _threadPool.awaitTermination (1, TimeUnit.SECONDS);
                } catch (InterruptedException exc) {
                }

                _impl.shutdown ();
            }
        });

        new SecuredExecutor (_config.getLogger ()).doSecured (new ThrowingRunnable () {
            public void run () {
                _config.getLogger ().logFinishedShutdown ();
            }
        });
    }
}
