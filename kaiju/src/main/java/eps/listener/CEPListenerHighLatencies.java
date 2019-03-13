package eps.listener;

import java.time.Instant;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import collector.RecordCollector;

/**
 * UpdateListener implementation saving incoming HighLatency3SigmaRule events as records to a given filepath.
 */
public class CEPListenerHighLatencies implements UpdateListener {
	
	private RecordCollector anomaliesCollector;
	
	/**
	 * Constructor of the class CEPListenerHighLatencies.
	 * @param filepath Filepath where records are saved (default value is {@code "./anomalies.csv"})
	 */
	public CEPListenerHighLatencies(String filepath) {
		if (filepath != null)
			anomaliesCollector = new RecordCollector(filepath, 0);
		else
			anomaliesCollector = new RecordCollector("./anomalies.csv", 0);
	}
	
	/**
	 * Update method saving incoming HighLatency3SigmaRule events as records to the filepath
	 * associated to the {@link CEPListenerHighLatencies CEPListenerHighLatencies} instance.
	 * Structure of the records is a {@code .csv} file comma-separated with columns 
	 * {@code traceId, spanId, operationName, serviceName, startTime, duration, hostname, recordTime}
	 */
	@Override
	public void update(EventBean[] newData, EventBean[] oldData) {
		
		if (newData.length > 0) {
         	for (EventBean e : newData) {
         		
         		String[] record = new String[8];
         		record[7] = Long.toString(Instant.now().toEpochMilli());
         		record[0] = (String) e.get("traceId");
         		record[1] = (String) e.get("spanId");
         		record[2] = (String) e.get("operationName");
         		record[3] = (String) e.get("serviceName");
         		record[4] = Long.toString((Long) e.get("startTime"));
         		record[5] = Long.toString((Long) e.get("duration"));
         		record[6] = (String) e.get("hostname");	
         		anomaliesCollector.addRecord(record);
         		
         	}
         } 
		
		

	}

}
