package eps.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import collector.RecordCollector;
import eps.EventToJsonConverter;

/**
 * UpdateListener implementation saving incoming {@link thriftgen.Span Span} events to a given filepath.
 */
public class CEPTailSamplingListener implements UpdateListener {
	
	private final static Logger log = LoggerFactory.getLogger(CEPTailSamplingListener.class);
	private RecordCollector sampledCollector;
	
	/**
	 * Constructor of the class CEPTailSamplingListener.
	 * @param filepath Filepath where events are saved (default value is {@code "./sampled.txt"})
	 */
	public CEPTailSamplingListener(String filepath) {
		if (filepath != null)
			sampledCollector = new RecordCollector(filepath, 0);
		else
			sampledCollector = new RecordCollector("./sampled.txt", 0);
	}
	
	/**
	 * Update method saving incoming {@link thriftgen.Span Span} events as records to the filepath
	 * associated to the {@link CEPTailSamplingListener CEPTailSamplingListener} instance.
	 * Structure of the records is a {@code .txt} file where each row is a JSON representation
	 * of a span.
	 */
	@Override
	public void update(EventBean[] newData, EventBean[] oldData) {
		
		try {
            if (newData.length > 0) {
            	
            	for (EventBean e : newData) {
            		String[] record = new String[1];
            		record[0] = EventToJsonConverter.spanFromEB(e);
            		sampledCollector.addRecord(record);
            		//log.info("Span  sampled");
            	}
            } 
        } catch (Exception e) {
			log.info(e.getStackTrace().toString());
			log.info(e.getMessage());
		}
		
	}
	

}
