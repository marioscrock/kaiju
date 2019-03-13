package eps.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

/**
 * Simple UpdateListener implementation logging incoming {@link com.espertech.esper.client.EventBean EventBean} objects together 
 * with a {@code String} message.
 */
public class CEPListener implements UpdateListener {
	
	private final static Logger log = LoggerFactory.getLogger(CEPListener.class);
	private String name;
	
	/**
	 * Constructor of the class CEPListener.
	 * @param name String to be logged together with incoming data.
	 */
	public CEPListener (String name) {
		this.name = name;
	}
	
	/**
	 * Update method logging incoming {@link com.espertech.esper.client.EventBean EventBean} objects {@code newData} together 
	 * with a {@code String} message associated to the {@link CEPListener CEPListener} instance.
	 */
	@Override
	public void update(EventBean[] newData, EventBean[] oldData) {
		
		StringBuilder sb = new StringBuilder();
		
		if (newData != null) {
			for (EventBean e : newData)
				sb.append(e.getUnderlying() + "\n");
		
			log.info(name + " " + sb.toString());
		}
		
	}
	

}
