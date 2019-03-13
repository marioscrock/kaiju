package eventsocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runnable class exposing a socket-server on port {@code 9876} accepting {@link eventSocket.Metric Metric}, {@link eventSocket.Flog FLog} and
 * {@link eventSocket.Event Event} event in JSON format from multiple clients to be sent the Esper engine.
 *
 */
public class EventSocketServer implements Runnable {
	
	public static int PORT = 9876;
	private final static Logger log = LoggerFactory.getLogger(EventSocketServer.class);
	private ExecutorService pool;
	
	/**
	 * Run implementation for the EventSocket class.
	 */
	@Override
	public void run() {
		
		ServerSocket serverSocket = null;
        Socket socket = null;
        pool = Executors.newCachedThreadPool();

        try {
            serverSocket = new ServerSocket(PORT);
            log.info("Server socket initilised on port " + PORT);
            
	        while (true) {
	            try {
	                socket = serverSocket.accept();
	            } catch (IOException e) {
	            	log.info("Exception " + e.getClass().getSimpleName() + ": " + e.getMessage());
	            } 
	            // new thread for a client
	            log.info("New socket client accepted on port " + PORT);
	            pool.submit(new EventSocketThread(socket));
	        }
        } catch (IOException e) {
			log.info("Exception " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }    
        finally {
        	try {
				socket.close();
			} catch (IOException e) {
				log.info("Exception " + e.getClass().getSimpleName() + ": " + e.getMessage());
			}
        }
        
	}   

}
