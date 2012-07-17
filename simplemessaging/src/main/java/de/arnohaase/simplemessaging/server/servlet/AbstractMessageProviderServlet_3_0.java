package de.arnohaase.simplemessaging.server.servlet;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.arnohaase.simplemessaging.common.Message;
import de.arnohaase.simplemessaging.common.MessageCategory;


/**
 * This servlet class is based on Servlet API 3.0, detaching threads from sockets and thus reducing footprint.
 * 
 * @author arno
 */
public abstract class AbstractMessageProviderServlet_3_0 extends AbstractMessageProviderServlet {
    /**
     * This method is for pre-filtering messages - the servlet can provide a list of message categories that the consumer should even be
     *  shown based on the user / request. Returning null causes all messages to be shown to the consumer.<br>
     *  
     * Messages that pass this first filter are then checked on a per-method basis by shouldReceive().
     */
    protected abstract Collection <MessageCategory> getRelevantCategories (HttpServletRequest request);

    /**
     * This callback method decides which messages are actually passed to the client side consumer. This is typically done based on the message
     *  CategoryDetails.<br>
     *  
     * This method has security implications. All messages passing this filter leave the trusted server
     *  environment and enter the untrusted client environment. NB: clients may be able to fake request parameters, HTTP headers etc. so those
     *  may not be a trustworthy foundation for making decisions here.
     */
    protected abstract boolean shouldReceive (HttpServletRequest request, Message msg);

    
    @Override
    public AbstractRequestProcessor createRequestProcessor (final HttpServletRequest request, HttpServletResponse response) {
        final long minInterestingMsgNumber = getMinMsgNumber (request);
        
        return new DetachedRequestProcessor (getMessaging (), request, getRelevantCategories (request)) {
            public long getMinInterestingMessageNumber () {
                return minInterestingMsgNumber;
            }
            
            public boolean shouldReceive (Message message) {
                return AbstractMessageProviderServlet_3_0.this.shouldReceive (request, message);
            }
        };
    }
}
