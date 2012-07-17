package de.arnohaase.simplemessaging.server.servlet;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.arnohaase.simplemessaging.common.Message;
import de.arnohaase.simplemessaging.common.MessageCategory;


/**
 * This class is based on Servlet API 2.x. However it performs a blocking wait for every request, i.e. every client permanently blocks
 *  a thread, increasing the footprint accordingly.<br>
 *  
 * If the application server supports Servlet API 3.0, another servlet class is available and should be used.
 * 
 * @author arno
 */
public abstract class AbstractMessageProviderServlet_2_x extends AbstractMessageProviderServlet {
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
        
        return new BlockingRequestProcessor (getMessaging (), request, response, getRelevantCategories (request)) {
            public long getMinInterestingMessageNumber () {
                return minInterestingMsgNumber;
            }

            public boolean shouldReceive (Message message) {
                return AbstractMessageProviderServlet_2_x.this.shouldReceive (request, message);
            }
        };
    }
}
