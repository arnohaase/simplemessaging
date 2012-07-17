package de.arnohaase.simplemessaging.client;

import java.util.Date;


public class MessagingClientConfiguration {
    private int _maxClientSideQueueSize = 1000;
    private int _delayBetweenPollingMillis = 0;
    private int _readerShutdownWaitMillis = 5000;
    private int _callbackShutdownWaitMillis = 100;

    private int _anzCallbackThreads = 1;
    
    private MessagingClientLogger _logger = MessagingClientLogger.COMMONS_LOGGER;
    private HttpHeaderProvider _headerProvider = HttpHeaderProvider.NULL_PROVIDER;

    private long _startingAtMessageNumber = 0;
    private Date _startingAtTimestamp = null;
    
    private int _socketTimeoutMillis = 6 * 60 * 1000;
    
    private long _failureIntervalBaseMillis = 1000;
    private double _failureIntervalFactor = 2.0;
    private long _failureIntervalMaxMillis = 5 * 60 * 1000;
    
    //TODO javadoc for the paramters

    public int getMaxClientSideQueueSize () {
        return _maxClientSideQueueSize;
    }
    
    public void setMaxClientSideQueueSize (int maxClientSideQueueSize) {
        _maxClientSideQueueSize = maxClientSideQueueSize;
    }
    
    public int getDelayBetweenPollingMillis () {
        return _delayBetweenPollingMillis;
    }
    
    public void setDelayBetweenPollingMillis (int delayBetweenPollingMillis) {
        _delayBetweenPollingMillis = delayBetweenPollingMillis;
    }
    
    public int getReaderShutdownWaitMillis () {
        return _readerShutdownWaitMillis;
    }
    
    public void setReaderShutdownWaitMillis (int readerShutdownWaitMillis) {
        _readerShutdownWaitMillis = readerShutdownWaitMillis;
    }
    
    public int getCallbackShutdownWaitMillis () {
        return _callbackShutdownWaitMillis;
    }
    
    public void setCallbackShutdownWaitMillis (int callbackShutdownWaitMillis) {
        _callbackShutdownWaitMillis = callbackShutdownWaitMillis;
    }
    
    public int getAnzCallbackThreads () {
        return _anzCallbackThreads;
    }
    
    public void setAnzCallbackThreads (int anzCallbackThreads) {
        _anzCallbackThreads = anzCallbackThreads;
    }
    
    public MessagingClientLogger getLogger () {
        return _logger;
    }
    
    public void setLogger (MessagingClientLogger logger) {
        _logger = logger;
    }
    
    public HttpHeaderProvider getHeaderProvider () {
        return _headerProvider;
    }
    
    public void setHeaderProvider (HttpHeaderProvider headerProvider) {
        _headerProvider = headerProvider;
    }
    
    public long getStartingAtMessageNumber () {
        return _startingAtMessageNumber;
    }
    
    public void setStartingAtMessageNumber (long startingAtMessageNumber) {
        _startingAtMessageNumber = startingAtMessageNumber;
    }
    
    public Date getStartingAtTimestamp () {
        return _startingAtTimestamp;
    }
    
    public void setStartingAtTimestamp (Date startingAtTimestamp) {
        _startingAtTimestamp = startingAtTimestamp;
    }
    
    public int getSocketTimeoutMillis () {
        return _socketTimeoutMillis;
    }
    
    public void setSocketTimeoutMillis (int socketTimeoutMillis) {
        _socketTimeoutMillis = socketTimeoutMillis;
    }

    public long getFailureIntervalBaseMillis () {
        return _failureIntervalBaseMillis;
    }

    public void setFailureIntervalBaseMillis (long failureIntervalBaseMillis) {
        _failureIntervalBaseMillis = failureIntervalBaseMillis;
    }

    public double getFailureIntervalFactor () {
        return _failureIntervalFactor;
    }

    public void setFailureIntervalFactor (double failureIntervalFactor) {
        _failureIntervalFactor = failureIntervalFactor;
    }

    public long getFailureIntervalMaxMillis () {
        return _failureIntervalMaxMillis;
    }

    public void setFailureIntervalMaxMillis (long failureIntervalMaxMillis) {
        _failureIntervalMaxMillis = failureIntervalMaxMillis;
    }
}
