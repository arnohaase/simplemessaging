package de.arnohaase.simplemessaging.server.messaging.impl.hibernate;

import org.hibernate.Session;


public interface HibernateJob <T> {
    T execute (Session s) throws Exception;
}
