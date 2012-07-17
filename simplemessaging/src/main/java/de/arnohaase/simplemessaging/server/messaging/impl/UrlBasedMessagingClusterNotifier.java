package de.arnohaase.simplemessaging.server.messaging.impl;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

import de.arnohaase.simplemessaging.server.messaging.config.MessagingClusterNotifier;
import de.arnohaase.simplemessaging.server.messaging.config.MessagingLogger;


public class UrlBasedMessagingClusterNotifier implements MessagingClusterNotifier {
    private final ExecutorService _threadPool;
    private final Collection<String> _serverUrls;
    private final MessagingLogger _logger;
    private final HttpClient _httpClient = new HttpClient (createHttpClientConfig());
    
    private static HttpClientParams createHttpClientConfig() {
        final HttpClientParams result = new HttpClientParams();
        result.setConnectionManagerClass(MultiThreadedHttpConnectionManager.class);
        return result;
    }
    
    public UrlBasedMessagingClusterNotifier (Collection<String> serverUrls, int anzNotificationThreads, MessagingLogger logger) {
        _threadPool = Executors.newFixedThreadPool (anzNotificationThreads);
        _serverUrls = serverUrls;
        _logger = logger;
    }
    
    private void notifyUrl (String serverUrl) {
        try {
            final GetMethod httpGet = new GetMethod (serverUrl);
            final int responseCode = _httpClient.executeMethod (httpGet);
            final String body = httpGet.getResponseBodyAsString ();

            if (responseCode != HttpStatus.SC_OK)
                _logger.logClusterNotificationFailure (serverUrl, responseCode, body, null);
        }
        catch (Exception exc) {
            _logger.logClusterNotificationFailure (serverUrl, 0, null, exc);
        }
    }

    public void notifyAllMessagingInstances () {
        //TODO leave out the sending cluster node
        for (final String serverUrl: _serverUrls) {
            _threadPool.execute (new Runnable () {
                public void run () {
                    notifyUrl (serverUrl);
                }
            });
        }
    }
    
    public void shutdown () {
        _threadPool.shutdown ();
        if (_httpClient.getHttpConnectionManager () instanceof MultiThreadedHttpConnectionManager)
            ((MultiThreadedHttpConnectionManager) _httpClient.getHttpConnectionManager ()).shutdown ();
    }
}
