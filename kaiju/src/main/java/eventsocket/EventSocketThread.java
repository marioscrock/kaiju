package eventsocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Single thread to handle a socket connection, parse received {@link eventSocket.Metric Metric}, {@link eventSocket.Flog FLog} and
 * {@link eventSocket.Event Event} event in JSON format and send them to the Esper engine.
 *
 */
public class EventSocketThread extends Thread {

    protected Socket socket;
    private final static Logger log = LoggerFactory.getLogger(EventSocketThread.class);

    public EventSocketThread(Socket clientSocket) {
        this.socket = clientSocket;
    }

    public void run() {
    	
        InputStream inp = null;
        BufferedReader brinp = null;
        try {
            inp = socket.getInputStream();
            brinp = new BufferedReader(new InputStreamReader(inp));
        } catch (IOException e) {
        	log.info("Exception " + e.getClass().getSimpleName() + ": " + e.getMessage());
            return;
        }
        
        String line;
        while (true) {
            try {
                line = brinp.readLine();
                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                    socket.close();
                    return;
                } else {
                	collector.Collector.executor.execute(new ParserJson(line));		
                }
            } catch (IOException e) {
            	log.info("Exception " + e.getClass().getSimpleName() + ": " + e.getMessage());
                return;
            }
        }
    }

}
