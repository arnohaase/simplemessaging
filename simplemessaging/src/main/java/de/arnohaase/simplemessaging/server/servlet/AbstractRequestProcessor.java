package de.arnohaase.simplemessaging.server.servlet;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import de.arnohaase.simplemessaging.common.MessageCategory;
import de.arnohaase.simplemessaging.server.messaging.Messaging;
import de.arnohaase.simplemessaging.server.messaging.iface.MessageConsumer;


abstract class AbstractRequestProcessor implements MessageConsumer {
    protected final HttpServletRequest _request;
    protected final Set<MessageCategory> _relevantCategories;
    protected final Messaging _messaging;

    public AbstractRequestProcessor (Messaging messaging, HttpServletRequest request, Collection<MessageCategory> relevantCategories) {
        _messaging = messaging;
        _request = request;
        _relevantCategories = asSet (relevantCategories);
    }

    private static Set<MessageCategory> asSet (Collection <MessageCategory> categories) {
        if (categories == null)
            return null;
        return Collections.unmodifiableSet (new HashSet<MessageCategory> (categories));
    }
    
    protected abstract void init ();
    protected abstract void afterRegistration () throws Exception;
    protected abstract void onSynchronousError ();
    
    public void process () throws Throwable {
        init ();
        _messaging.registerConsumer (this);
        afterRegistration ();
    }
    
    public final Set<MessageCategory> getRelevantCategories () {
        return _relevantCategories;
    }
}
