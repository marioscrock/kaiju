package eventsocket;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a structured log.
 *
 */
public class FLog implements Serializable {

	private static final long serialVersionUID = 709484586326437353L;
	
	public Map<String, Object> fields = new HashMap<String, Object>();	
	
	public FLog(Map<String, Object> fields) {
		this.fields = fields;	
	}
	
	public Map<String, Object> getFields() {
		return fields;
	}
	
	public void setFields(Map<String, Object> fields) {
		this.fields = fields;
	}
	
	@Override
	public String toString() {
		return "FLog [fields=" + fields + "]";
	}
	
}