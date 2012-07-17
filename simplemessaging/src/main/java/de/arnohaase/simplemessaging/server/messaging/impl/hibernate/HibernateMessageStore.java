package de.arnohaase.simplemessaging.server.messaging.impl.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.transaction.Status;
import javax.transaction.Synchronization;

import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.arnohaase.simplemessaging.common.Message;
import de.arnohaase.simplemessaging.common.MessageCategory;
import de.arnohaase.simplemessaging.server.messaging.config.MessageStore;


public class HibernateMessageStore implements MessageStore {
    private final SessionFactory _sf;
    
    public HibernateMessageStore (SessionFactory sf) throws Exception {
        _sf = sf;
        ensureLockEntity ();
    }

    private void ensureLockEntity () throws Exception{
        doWithSession (new HibernateJobWithoutResult() {
            public void execute (Session s) throws Exception {
                if (s.get (HibernateMessagingLock.class, HibernateMessagingLock.THE_SINGLE_ID) == null) {
                    try {
                        s.save (new HibernateMessagingLock (HibernateMessagingLock.THE_SINGLE_ID));
                    }
                    catch (Exception exc) {
                        // this is safe to ignore - it means several cluster nodes are starting
                        exc.printStackTrace ();
                    }
                }
            }
        });
    }
    
    private void lock (Session s) {
        if (null == s.get (HibernateMessagingLock.class, HibernateMessagingLock.THE_SINGLE_ID, LockMode.UPGRADE))
            throw new IllegalStateException ("HibernateMessageLock entity is missing in the database");
    }

    /**
     * This hook can be overridden so that messages can be provided in an application transaction even without JTA. If
     *  there is no 'current' session then this method must return null, causing the MessageStore to perform its operation
     *  in its own session. That is the default.
     */
    public Session findCurrentSessionToJoin () {
        return null;
    }
    
    private <T> T doWithSession (HibernateJob<T> job) throws Exception {
        final Session currentSession = findCurrentSessionToJoin ();
        
        if (currentSession == null) {
            final Session s = _sf.openSession ();

            try {
                s.beginTransaction ();
                final T result = job.execute (s);
                s.getTransaction ().commit ();
                return result;
            }
            finally {
                s.close ();
            }
        }
        else {
            // transaction handling and closing the Session are the applications's responsibility
            return job.execute (currentSession);
        }
    }

    private void doWithSession (final HibernateJobWithoutResult job) throws Exception {
        doWithSession (new HibernateJob<Object> () {
            public Object execute (Session s) throws Exception {
                job.execute (s);
                return null;
            }
        });
    }

    @Override
    public synchronized void addMessage (final MessageCategory category, final String categoryDetails, final Object msgData, final Runnable publish) throws Exception {
        doWithSession (new HibernateJobWithoutResult () {
            public void execute (Session s) throws Exception {
                lock (s); // SELECT FOR UPDATE to a well-known row --> storage of messages is serialized across all nodes
                s.save (new HibernateMessage (new Date (), category.getName (), categoryDetails, msgData));
                
                s.getTransaction ().registerSynchronization (new Synchronization () {
                    public void beforeCompletion () {
                    }

                    public void afterCompletion (int status) {
                        if (status == Status.STATUS_COMMITTED)
                            publish.run ();
                    }
                });
            }
        });
    }
    
    @SuppressWarnings ("unchecked")
    public synchronized long getNextMessageNumber () throws Exception {
        return doWithSession (new HibernateJob <Long> () {
            public Long execute (Session s) throws Exception {
                final List<Long> l = s.createQuery ("select max (msgNo) from HibernateMessage").list ();
                if (l.isEmpty () || l.get (0) == null)
                    return INITIAL_MESSAGE_NUMBER; 
                return l.get (0) + 1;
            }
        });
    }

    @SuppressWarnings ("unchecked")
    @Override
    public long getMsgNumberForTimestamp (final Date timestamp) throws Exception {
        return doWithSession (new HibernateJob<Long>() {
            public Long execute (Session s) throws Exception {
                final Query q = s.createQuery ("select min (msgNo) from HibernateMessage where msgTimestamp >= ?");
                q.setParameter (0, timestamp);
                final List<Long> l = q.list ();
                if (l.isEmpty () || l.get (0) == null)
                    return getNextMessageNumber ();
                return l.get (0);
            }
        });
    }
    
    @SuppressWarnings ("unchecked")
    @Override
    public synchronized List <Message> getMessages (final long minInterestingMessageNumber, final Collection<MessageCategory> categories) throws Exception {
        if (categories != null && categories.isEmpty ())
            return new ArrayList<Message> ();
        
        return doWithSession (new HibernateJob <List <Message>> () {
            public List <Message> execute (Session s) throws Exception {
                final Query q = s.createQuery (createSelectHql (categories).toString ());
                setSelectParameters (minInterestingMessageNumber, categories, q);
                
                return q.list ();
            }

            private void setSelectParameters (final long minInterestingMessageNumber, final Collection<MessageCategory> categories, final Query q) {
                q.setParameter (0, minInterestingMessageNumber);
                if (categories != null) {
                    int i=1;
                    for (MessageCategory category: categories)
                        q.setParameter (i++, category.getName ());
                }
            }

            private String createSelectHql (final Collection<MessageCategory> categories) {
                final StringBuilder hql = new StringBuilder ("select m from HibernateMessage m where msgNo >= ?");
                if (categories != null) {
                    hql.append (" and category in (?");
                    
                    for (int i=1; i<categories.size (); i++)
                        hql.append (", ?");
                    
                    hql.append (")");
                }
                hql.append (" order by msgNo asc");
                return hql.toString ();
            }
        });
    }

    private Date readMaxTimestamp (Session s) {
        try { 
            // convert a Timestamp to a real java.util.Date --> comparison between the two does not work in Java
            return new Date (((Date) s.createQuery ("select max(msgTimestamp) from HibernateMessage").uniqueResult ()).getTime ());
        }
        catch (Exception exc) {
            return null;
        }
    }
    
    private void removeMessagesBefore (Session s, Date cutOffTimestamp) {
        if (cutOffTimestamp == null)
            return;

        // ensure that at least one message remains in order to maintain a gapless sequence of messages
        final Date maxTimestamp = readMaxTimestamp (s);
        if (maxTimestamp == null)
            return;
        
        final Query deleteQuery = s.createQuery ("delete from HibernateMessage where msgTimestamp < ?");
        deleteQuery.setParameter (0, maxTimestamp.after (cutOffTimestamp) ? cutOffTimestamp : maxTimestamp);
        deleteQuery.executeUpdate ();
    }

    private void reduceMessageCount (Session s, int maxNumMessages) {
        if (maxNumMessages <= 0)
            return;

        // race condition - if several jobs clean up at the same time, up to (#jobs -1) more messages than specified may be deleted. 
        //  Applications must take care of this by limiting the number of concurrent cleanup jobs or specifying an accordingly bigger
        //  maximum number of messages.
        
        while (readMessageCount (s) > maxNumMessages)
            deleteFirstMessage (s);
    }
    
    private void deleteFirstMessage (Session s) {
        // This query relies on there being at least one message in the database. This is ensured if the maximum number of concurrent cleanup
        //  threads is smaller than the the configured maximum number of messages. This should be true for any normal system.
        
        final HibernateMessage m = (HibernateMessage) s.createQuery ("select m from HibernateMessage m order by msgNo asc").iterate ().next ();
        s.delete (m);
    }

    private int readMessageCount (Session s) {
        return ((Number) s.createQuery ("select count(*) from HibernateMessage").uniqueResult ()).intValue ();
    }

    @Override
    public synchronized void removeOutdatedMessages (final Date cutOffTimestamp, final int maxNumMessages) throws Exception {
        doWithSession (new HibernateJobWithoutResult() {
            public void execute (Session s) throws Exception {
                removeMessagesBefore (s, cutOffTimestamp);
                reduceMessageCount (s, maxNumMessages);
            }
        });
    }

    @Override
    public void shutdown () {
    }
}





