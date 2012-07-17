package de.arnohaase.simplemessaging.server.messaging.impl.hibernate;

import org.hibernate.Session;


public interface HibernateJobWithoutResult {
    void execute (Session s) throws Exception;
}
