package eps.listener;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import collector.RecordCollector;

/**
 * Simple UpdateListener implementation saving to file the incoming {@link com.espertech.esper.client.EventBean EventBean} objects together 
 * with a {@code String} message.
 */
public class CEPListenerRecord implements UpdateListener {
	
	private RecordCollector recordCollector;

	private String name;
	
	/**
	 * Constructor of the class CEPListenerRecord.
	 * @param name String to be saved together with incoming data.
	 * @param filepath Filepath where records are saved (default value is {@code "./"+ name + ".txt"})
	 */
	public CEPListenerRecord (String name, String filepath) {
		this.name = name;
		if (filepath != null)
			recordCollector = new RecordCollector(filepath, 0);
		else
			recordCollector = new RecordCollector("./"+ name + ".txt", 0);
	}
	
	/**
	 * Update method saving to file incoming {@link com.espertech.esper.client.EventBean EventBean} objects {@code newData} together 
	 * with a {@code String} message associated to the {@link CEPListenerRecord CEPListenerRecord} instance.
	 */
	@Override
	public void update(EventBean[] newData, EventBean[] oldData) {
		
		StringBuilder sb = new StringBuilder();
		
		if (newData != null) {
			for (EventBean e : newData)
				sb.append(e.getUnderlying() + "\n");
		
			recordCollector.addRecord(new String[]{(name + " " + sb.toString())});
		}
		
	}
	

}
