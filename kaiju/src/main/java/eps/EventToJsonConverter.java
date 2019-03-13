package eps;

import java.util.List;

import com.espertech.esper.client.EventBean;

import thriftgen.Log;
import thriftgen.Span;
import thriftgen.SpanRef;
import thriftgen.Tag;

/**
 * Class to convert {@link com.espertech.esper.client.EventBean EventBean} objects to a specific JSON representation.
 *
 */
public class EventToJsonConverter {
	
	/**
	 * Static method to convert an {@link com.espertech.esper.client.EventBean EventBean} objects containing a
	 * {@link thriftgen.Span Span} event to its JSON representation. 
	 * @param e The {@link com.espertech.esper.client.EventBean EventBean} to convert
	 * @return The JSON {@code String} representing the input
	 */
	public static String spanFromEB(EventBean e) {
    	
    	Span s = (Span) e.get("span");
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("{ ");
    	sb.append("\"traceID\" : ");
    	sb.append("\"" + traceIdToHex(s.getTraceIdHigh(), s.getTraceIdLow()) + "\", ");
    	sb.append("\"serviceName\" : ");
    	sb.append("\"" + e.get("serviceName") + "\", ");
    	sb.append("\"hashProcess\" : ");
    	sb.append("\"" + e.get("hashProcess") + "\", ");
    	sb.append("\"spanID\" : ");
    	sb.append("\"" + Long.toHexString(s.getSpanId()) + "\", ");
    	sb.append("\"parentSpanID\" : ");
    	sb.append("\"" + Long.toHexString(s.getParentSpanId()) + "\", ");
    	sb.append("\"operationName\" : ");
    	sb.append("\"" + s.getOperationName() + "\", ");
    	
    	sb.append("\"references\" : [");
    	List<SpanRef> spanrefs =  s.getReferences();
    	if (spanrefs.size() > 0) {
	    	for(int i = 0; i < spanrefs.size(); i ++) {
	    		sb.append(" { ");
	    		sb.append("\"traceID\" : ");
	    		sb.append(" \"" + traceIdToHex(spanrefs.get(i).getTraceIdHigh(), spanrefs.get(i).getTraceIdLow()) + "\", ");
	    		sb.append("\"spanID\" : ");
	    		sb.append(" \"" + Long.toHexString(spanrefs.get(i).getSpanId()) + "\", ");
	    		sb.append("\"refType\" : ");
	    		sb.append(" \"" + spanrefs.get(i).getRefType().toString() + "\"");
	    		sb.append("},");
	    	}
	    	sb.deleteCharAt(sb.length() - 1);   	
    	} 
    	sb.append(" ], ");
    	
    	sb.append("\"startTime\" : ");
    	sb.append("\"" + s.getStartTime() + "\", ");
    	sb.append("\"duration\" : ");
    	sb.append("\"" + s.getDuration() + "\", ");
    	sb.append("\"flags\" : ");
    	sb.append("\"" + s.getFlags() + "\", ");	
    	
    	sb.append("\"tags\" : [");
    	List<Tag> tags =  s.getTags();
    	if (tags.size() > 0) {
	    	for(int i = 0; i < tags.size(); i ++) {
	    		sb.append(" { ");
	    		sb.append("\"key\" : ");
	    		sb.append(" \"" + tags.get(i).getKey() + "\", ");
	    		sb.append("\"value\" : ");
	    		sb.append(" \"" + tags.get(i).getKey() + "\", ");
	    		sb.append("\"type\" : ");
	    		sb.append(" \"" + tags.get(i).getVType().toString() + "\"");
	    		sb.append("},");
	    	}
	    	sb.deleteCharAt(sb.length() - 1);
    	} 
    	sb.append(" ], ");
    	
    	sb.append("\"logs\" : [");
    	List<Log> logs =  s.getLogs();
    	if (logs.size() > 0) {
	    	for(int i = 0; i < logs.size(); i ++) {
	    		sb.append("{ ");
	    		sb.append("\"timestamp\" : ");
	    		sb.append(" \"" + logs.get(i).getTimestamp() + "\", ");
	    		sb.append("\"fields\" : [");
	    		for (Tag f : logs.get(i).getFields()) {
	    			sb.append(" { ");
		    		sb.append("\"key\" : ");
		    		sb.append(" \"" + f.getKey() + "\", ");
		    		sb.append("\"value\" : ");
		    		sb.append(" \"" + f.getKey() + "\", ");
		    		sb.append("\"type\" : ");
		    		sb.append(" \"" + f.getVType().toString() + "\"");
		    		sb.append("},");
	    		}
	    		sb.deleteCharAt(sb.length() - 1);
	        	sb.append(" ]");	
	    		
	        	sb.append("},");
	    	}
	    	sb.deleteCharAt(sb.length() - 1);
    	} 
    	sb.append(" ]");
    	
    	sb.append(" }");   	
    	return sb.toString();
    	
    }
	
	/**
	 * Convert {@link thriftgen.Span#traceIdHigh traceIdHigh} and {@link thriftgen.Span#traceIdLow traceIdLow}
	 * to their HEX representation.
	 * @param traceIdHigh
	 * @param traceIdLow
	 * @return String containing the HEX value 
	 */
	public static String traceIdToHex(Long traceIdHigh, Long traceIdLow) {
		
		if (traceIdHigh == 0)
			return String.format("%x", traceIdLow);
		else
			return String.format("%x%016x", traceIdHigh, traceIdLow);
		
	}
	
	//Provides an hash for each Process, same hash if same serviceName
	//and same tags(despite of their order)
	public static int hashProcess(thriftgen.Process process) {
		
		int result = 0;
		//37 - must be prime
		result = 37 * result + process.getServiceName().hashCode();
		
		int tagsHash = 0;
		if (process.getTags() != null) {
			for (Tag tag : process.getTags())
				tagsHash += tag.hashCode();
		}
		result = 37 * result + tagsHash;
		
		return Math.abs(result); 
		
	}

}
