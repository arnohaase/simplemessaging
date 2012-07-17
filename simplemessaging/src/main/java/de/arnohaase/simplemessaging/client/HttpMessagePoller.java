package de.arnohaase.simplemessaging.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import de.arnohaase.simplemessaging.common.Message;
import de.arnohaase.simplemessaging.common.MessagingConstants;
import de.arnohaase.simplemessaging.common.serialize.MessageSerializer;


/**
 * This class performs the actual communication with the server, including error handling. It is intended
 *  for internal use.<br>
 *  
 *  It does not perform the actual thread handling of the background reader thread, but it takes care of increasing
 *   delays in case of an error.
 * 
 * @author arno
 */
class HttpMessagePoller {
    private final MessagingClientLogger _logger;
    private final HttpHeaderProvider _headerProvider;
    
    private final String _serverUrl;
    private final int _socketTimeoutMillis;
    
    private Date _startingAtTimestamp = null;
    private long _nextInterestingMessage = 0;

    private int _anzFehlgeschlageneCalls = 0;

    private final long _failureIntervalBase;
    private final double _failureIntervalFactor;
    private final long _failureIntervalMax;
    
    final HttpClient _httpClient = new HttpClient ();


    public HttpMessagePoller (String serverUrl, MessagingClientLogger logger, HttpHeaderProvider headerProvider, 
            int socketTimeoutMillis, Date startingAtTimestamp, long startingAtMessageNumber,
            long failureIntervalBase, double failureIntervalFactor, long failureIntervalMax) {
        _serverUrl = serverUrl;
        _logger = logger;
        _headerProvider = headerProvider;
        _socketTimeoutMillis = socketTimeoutMillis;
        _startingAtTimestamp = startingAtTimestamp;
        _nextInterestingMessage = startingAtMessageNumber;
        
        _failureIntervalBase = failureIntervalBase;
        _failureIntervalFactor = failureIntervalFactor;
        _failureIntervalMax = failureIntervalMax;

        _httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler (0, false));
        _httpClient.getParams ().setSoTimeout (_socketTimeoutMillis);
    }

    public List<Message> readFromServer () {
        try {
            final long interval = nextPollingIntervalMillis ();
            if (_anzFehlgeschlageneCalls > 0)
                _logger.logFailureWait (_anzFehlgeschlageneCalls, interval);
            
            Thread.sleep (interval);
        } catch (InterruptedException exc) {
            _logger.logInterruptedException (exc);
        }
        
        try {
            return readFromServerInternal ();
        }
        catch (Exception exc) {
            _logger.logTransmissionError (exc);
        }
        _anzFehlgeschlageneCalls++;
        return new ArrayList <Message> ();
    }


    private long nextPollingIntervalMillis () {
        if (_anzFehlgeschlageneCalls == 0)
            return 0;
        
        long result = _failureIntervalBase;
        for (int i=0; i<_anzFehlgeschlageneCalls; i++) {
            result *= _failureIntervalFactor;
            if (result > _failureIntervalMax)
                return _failureIntervalMax;
        }
        
        return result;
    }

    @SuppressWarnings ("unchecked")
    private List<Message> readFromServerInternal () throws Exception {
        final HttpMethod httpGet = new GetMethod (serverUrlWithParams ());
        addCustomHeaders (httpGet);

        try {
            final long beforeMillis = System.currentTimeMillis ();
            final int responseCode = _httpClient.executeMethod (httpGet);
            final long callDuration = System.currentTimeMillis () - beforeMillis;

            if (responseCode != HttpStatus.SC_OK) {
                handleServerError (responseCode, httpGet);
                _anzFehlgeschlageneCalls++;
                return new ArrayList<Message> ();
            }

            final InputStream in = httpGet.getResponseBodyAsStream ();
            final List<Message> result = (List<Message>) new MessageSerializer ().deserialize (in);
            rememberAsReceived (result);

            _anzFehlgeschlageneCalls = 0;
            _logger.logSuccessfulCall (result.size (), responseCode, callDuration);
            return result;
        }
        finally {
            httpGet.releaseConnection ();
        }
    }

    private void addCustomHeaders (final HttpMethod httpGet) {
        for (Header header: _headerProvider.getCustomHeaders ())
            httpGet.addRequestHeader (header);
    }

    private void handleServerError (int responseCode, HttpMethod httpGet) throws IOException {
        _logger.logServerError (responseCode, httpGet.getResponseBodyAsString ());
    }

    private void rememberAsReceived (final List<Message> result) {
        if (! result.isEmpty ())
            _startingAtTimestamp = null; // only when a message was received successfully
        
        for (Message m: result) {
            if (m.getSeqNumber () >= _nextInterestingMessage)
                _nextInterestingMessage = m.getSeqNumber () + 1;
        }
    }

    private String serverUrlWithParams () {
        final StringBuilder params = new StringBuilder ();
        if (_startingAtTimestamp != null)
            params.append (MessagingConstants.STARTING_AT_TIMESTAMP_MILLIS + "=" + _startingAtTimestamp.getTime ());
        else
            params.append (MessagingConstants.MIN_MSG_NUMBER_PARAM_NAME + "=" + _nextInterestingMessage);
        
        return _serverUrl + "?" + params;
    }
    
    public void shutdown () {
        if (_httpClient.getHttpConnectionManager () instanceof MultiThreadedHttpConnectionManager)
            ((MultiThreadedHttpConnectionManager) _httpClient.getHttpConnectionManager ()).shutdown ();
    }
}


