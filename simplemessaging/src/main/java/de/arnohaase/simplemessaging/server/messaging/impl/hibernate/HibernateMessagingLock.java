package de.arnohaase.simplemessaging.server.messaging.impl.hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class HibernateMessagingLock {
    public static final long THE_SINGLE_ID = 1L;
    
    private long _oid;

    public HibernateMessagingLock () {
    }

    public HibernateMessagingLock (long oid) {
        _oid = oid;
    }
    
    @Id
    public long getOid () {
        return _oid;
    }
    
    public void setOid (long oid) {
        _oid = oid;
    }
}
