package eps;

import thriftgen.*;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;

import eventsocket.Event;
import eventsocket.FLog;
import eventsocket.Metric;
import mode.Mode;

/**
 * Class to manage the Esper Engine.
 */
public class EsperHandler {
	
	public static String RETENTION_TIME = "2min";
	public static Mode MODE;	
	public static EPRuntime cepRT;
	public static EPAdministrator cepAdm;
	
	/**
	 * Static method to initialize the Esper Engine and the selected statements.
	 */
	public static void initializeHandler() {
		
		//Check not already initialized
		if(cepRT == null) {
			
			//The Configuration is meant only as an initialization-time object.
		    Configuration cepConfig = new Configuration();
		    
		    // Basic Events
		    MODE.addEventTypes(cepConfig);
		 
		    // We setup the engine
		    EPServiceProvider cep = EPServiceProviderManager.getProvider("myCEPEngine", cepConfig);
		    
		    cepRT = cep.getEPRuntime();
		    cepAdm = cep.getEPAdministrator();
		    
		    MODE.addStatements(cepAdm, true);
		   
		}
	    
	}

	/**
	 * Static method to send a {@link thriftgen.Batch Batch} event, and a {@link thriftgen.Span Span} for each span 
	 * in the batch to the Esper engine.
	 * @param batch The {@link thriftgen.Batch Batch} to be sent to the Esper engine.
	 */
	public static void sendBatch(Batch batch) {
		
		cepRT.sendEvent(batch);
		
		if(batch.getSpans() != null) {
			
			for(Span span : batch.getSpans()) {
				cepRT.sendEvent(span);			
			}
		}
	}
	
	/**
	 * Static method to send a {@link eventsocket.Metric Metric} event to the Esper engine.
	 * @param metric The {@link eventsocket.Metric Metric} to be sent to the Esper engine.
	 */
	public static void sendMetric(Metric metric) {
		
		cepRT.sendEvent(metric);
		
	}
	
	/**
	 * Static method to send a {@link eventsocket.Event Event} event to the Esper engine.
	 * @param event The {@link eventsocket.Event Event} to be sent to the Esper engine.
	 */
	public static void sendEvent(Event event) {

		cepRT.sendEvent(event);
		
	}
	
	/**
	 * Static method to send a {@link eventsocket.FLog FLog} event to the Esper engine.
	 * @param event The {@link eventsocket.FLog FLog} to be sent to the Esper engine.
	 */
	public static void sendFLog(FLog flog) {

		cepRT.sendEvent(flog);
		
	}

}