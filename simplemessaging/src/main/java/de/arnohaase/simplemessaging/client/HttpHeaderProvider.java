package de.arnohaase.simplemessaging.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Header;


/**
 * This callback interface exists to provide HTTP headers. The method <code>getCustomHeaders()</code>
 *  is called for every HTTP request.<br>
 * 
 * The reason headers are not simply passed into the MessagingClient on initialization is the possibility
 *  of reverse proxies between client and server: On reconnect the application may perform a new handshake
 *  with the reverse proxy, possibly creating a new session with a new session cookie. Using a callback
 *  allows an application to pass the valid cookie for every call.
 * 
 * @author arno
 */
public interface HttpHeaderProvider {
    List<Header> getCustomHeaders ();
    
    HttpHeaderProvider NULL_PROVIDER = new HttpHeaderProvider() {
        @Override
        public List<Header> getCustomHeaders () {
            return new ArrayList<Header> ();
        }
    };
}
