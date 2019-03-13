package mode;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;

import eps.EsperHandler;
import eventsocket.EventSocketServer;

/**
 * Mode interface.
 *
 */
public interface Mode {
	
	default public void init() throws InterruptedException {	
		
		EsperHandler.initializeHandler();
		
		//Open Events Socket
    	EventSocketServer es = new EventSocketServer();
    	Thread eventSocketThread = new Thread(es);
    	eventSocketThread.start();
    	
	}
	
	public void addEventTypes(Configuration cepConfig);
	
	public void addStatements(EPAdministrator cepAdm, boolean parse);	

}
