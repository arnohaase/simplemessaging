package client;


import de.arnohaase.simplemessaging.client.MessageListener;
import de.arnohaase.simplemessaging.client.MessagingClientConfiguration;
import de.arnohaase.simplemessaging.client.SimpleMessagingClient;
import de.arnohaase.simplemessaging.common.Message;


public class SimpleMessagingClientMain {
//    public static final String SERVER_URL = "http://localhost:8080/smd/sync"; // Servlet API 2.x
//    public static final String SERVER_URL = "http://localhost:8080/smd/async"; // Servlet API 3.0 
    public static final String SERVER_URL = "http://localhost:8080/ap1/async"; // Servlet API 3.0 
    private static final MessagingClientConfiguration _config = new MessagingClientConfiguration ();
    
    public static void main (String[] args) throws Exception {
        _config.setStartingAtMessageNumber (1);
        
        doPolling ();
//        doCallback ();
    }

    @SuppressWarnings ("unused")
    private static void doCallback () {
        final SimpleMessagingClient messageConsumer = SimpleMessagingClient.createForCallback (SERVER_URL, _config, new MessageListener () {
            public void onMessage (Message o) {
                System.out.println (Thread.currentThread ().getName () + ": " + o);
            }
        }); 
    }
    private static void doPolling () {
        final SimpleMessagingClient messageConsumer = SimpleMessagingClient.createForPolling (SERVER_URL, _config); 
        
        while (true) {
            System.out.println (messageConsumer.blockingGet ());
        }
    }
}
