package server;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;


import de.arnohaase.simplemessaging.common.Message;
import de.arnohaase.simplemessaging.common.MessageCategory;
import de.arnohaase.simplemessaging.server.servlet.AbstractMessageProviderServlet_3_0;


public class AsyncPollingServlet extends AbstractMessageProviderServlet_3_0 {
    @Override
    protected Collection<MessageCategory> getRelevantCategories (HttpServletRequest request) {
        return null;
    }

    @Override
    protected boolean shouldReceive (HttpServletRequest request, Message msg) {
        return true;
    }
}
