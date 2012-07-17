package de.arnohaase.simplemessaging.server.messaging.impl.hibernate;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import de.arnohaase.simplemessaging.common.Message;
import de.arnohaase.simplemessaging.server.messaging.config.MessageStore;
import de.arnohaase.simplemessaging.server.messaging.impl.AbstractMessageStoreTest;
import de.arnohaase.simplemessaging.server.messaging.impl.hibernate.HibernateMessage;
import de.arnohaase.simplemessaging.server.messaging.impl.hibernate.HibernateMessageStore;
import de.arnohaase.simplemessaging.server.messaging.impl.hibernate.HibernateMessagingLock;


public class HibernateMessageStoreTest extends AbstractMessageStoreTest {
    private static Configuration _config = createConfiguration ();
    private static SessionFactory _sf = _config.buildSessionFactory ();
    
    @Override
    protected void setUp () throws Exception {
        new SchemaExport (_config).create (false, true);
    }

    @Override
    public MessageStore createMessageStore () throws Exception {
        return new HibernateMessageStore (_sf);
    }
    
    public void testConcurrency () throws Exception {
        final Thread[] threads = new Thread[5];
        for (int threadNumber=0; threadNumber < 5; threadNumber++) {
            threads[threadNumber] = new Thread () {
                public void run() {
                    try {
                        final MessageStore msgStore = new HibernateMessageStore (_sf);
                        for (int msgInThread = 0; msgInThread < 100; msgInThread++) {
                            msgStore.addMessage (CAT_A, "de", "data", NULL_RUNNABLE);
                            final List<Message> messages = msgStore.getMessages (0, null);
                            for (int i = 0; i < messages.size (); i++)
                                assertEquals (MessageStore.INITIAL_MESSAGE_NUMBER + i, messages.get (i).getSeqNumber ());
                        }
                        msgStore.shutdown ();
                    }
                    catch (Exception exc) {
                        exc.printStackTrace ();
                        fail (exc.toString ());
                    }
                }
            };
            threads[threadNumber].start ();
        }
        
        for (int i=0; i<5; i++)
            threads[i].join ();
    }
    
    private static Configuration createConfiguration () {
        final AnnotationConfiguration config = new AnnotationConfiguration ();
        config.addAnnotatedClass (HibernateMessage.class);
        config.addAnnotatedClass (HibernateMessagingLock.class);
        
        config.setProperty ("hibernate.dialect",                 "org.hibernate.dialect.H2Dialect");
        config.setProperty ("hibernate.connection.driver_class", "org.h2.Driver");
        config.setProperty ("hibernate.connection.username",     "sa");
        config.setProperty ("hibernate.connection.password",     "");
        config.setProperty ("hibernate.connection.url",          "jdbc:h2:testdb/message-store-test");
        
        config.setProperty ("hibernate.hbm2ddl.auto", "create"); // create-drop / create / update / validate
        
        config.setProperty ("hibernate.connection.provider_class", "org.hibernate.connection.C3P0ConnectionProvider");
        config.setProperty ("hibernate.c3p0.max_size", "20");
        config.setProperty ("hibernate.c3p0.min_size", "2");
        
        config.setProperty ("hibernate.show_sql",   "true");

        return config;
    }
}
