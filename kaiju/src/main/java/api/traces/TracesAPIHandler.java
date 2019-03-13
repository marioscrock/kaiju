package api.traces;

import com.espertech.esper.client.EPOnDemandQueryResult;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;

import eps.EsperHandler;
import eps.EventToJsonConverter;
import eps.listener.CEPListener;
import spark.Request;
import spark.Response;

/**
 * Class to handle KaijuAPI requests.
 */
public class TracesAPIHandler {
	
	/**
	 * Static method to handle {@code POST /api/query?query=<query>}.
	 * @param request The request to handle.
	 * @param response The response.
	 * @return The string to be sent as response.
	 */
	public static String fireQuery(Request request, Response response) {
		
		String query = request.queryParams("query");
		EPOnDemandQueryResult result = EsperHandler.cepRT.executeQuery(query);
		
		response.type("application/json");
		response.status(200);
		
		StringBuilder sb = new StringBuilder();
		
		response.type("application/json");
		if (result != null) 
            if (result.getArray().length > 0) {
            	for (EventBean e : result.getArray())
            		sb.append(e.getUnderlying() + "\n");
		}
		
		return "{ \"result\":\"" + sb.toString() + "\"}"; 
		
	}
	
	/**
	 * Static method to handle {@code POST /api/statement?statement=<statement>&msg=<msg>}.
	 * @param request The request to handle.
	 * @param response The response.
	 * @return The string to be sent as response.
	 */
	public static String registerStatement(Request request, Response response) {
		
		EPStatement statement = EsperHandler.cepAdm.createEPL(request.queryParams("statement"));
	    statement.addListener(new CEPListener(request.queryParams("msg")));
		
	    response.type("application/json");
		response.status(200);
		
		return "{ \"statementName\":\"" + statement.getName() + "\"}";
	}
	
	/**
	 * Static method to handle {@code POST /api/remove?statement=<stmt_code>}.
	 * @param request The request to handle.
	 * @param response The response.
	 * @return The string to be sent as response.
	 */
	public static String removeStatement(Request request, Response response) throws Exception {
		
		String stmtName = request.queryParams("statement");
		EPStatement statement = EsperHandler.cepAdm.getStatement(stmtName);
		if (statement != null) {
		    statement.destroy();
		    response.type("application/json");
			response.status(200);
		} else {
			throw new Exception("No statement '" + stmtName + "' found");
		}
		
		return "{}";
	}
	
	/**
	 * Static method to handle {@code GET /api/traces/all}.
	 * @param request The request to handle.
	 * @param response The response.
	 * @return The string to be sent as response.
	 */
	public static String getAllTracesIds(Request request, Response response) {
		
		EPOnDemandQueryResult result = null;
		
		result = TracesAPIQueries.preparedTraces.execute();
	
		response.type("application/json");
		if (result != null) 
            if (result.getArray().length > 0) {
            	
            	StringBuilder sb = new StringBuilder();
            	sb.append("{ \"traceIDsHex\":[");
            	for (EventBean row : result.getArray()) {
            		  sb.append(" \"" + row.get("traceIdHex") + "\",");
            	}
            	sb.deleteCharAt(sb.length() - 1);
            	sb.append("] }");
            	
            	return sb.toString();
            	
            } 
        
		return "{ \"traceIDsHex\":[]}";
	}
	
	/**
	 * Static method to handle {@code GET /api/traces/:id}.
	 * @param request The request to handle.
	 * @param response The response.
	 * @return The string to be sent as response.
	 */
	public static String getTraceByTraceId(Request request, Response response) {
		
		response.type("application/json");
        
        String traceId = request.params(":id");
        EPOnDemandQueryResult result = null;
        
        synchronized (response) {
        	TracesAPIQueries.preparedSpansTraceId.setObject(1, traceId);
        	result = EsperHandler.cepRT.executeQuery(TracesAPIQueries.preparedSpansTraceId);
        }
        

		if (result != null) 
            if (result.getArray().length > 0) {
            	
            	StringBuilder sb = new StringBuilder();
            	sb.append("{ \"spans\":[");
            	
            	for (EventBean e : result.getArray()) {
            		sb.append(" ");
            		sb.append(EventToJsonConverter.spanFromEB(e));
            		sb.append(",");
            	}
            		
            	sb.deleteCharAt(sb.length() - 1);
            	sb.append("] }");
            	
            	return sb.toString();
            } 
        
		return "{ \"spans\":[]}";
	}
	
	/**
	 * Static method to handle {@code GET /api/traces?service=<service>}.
	 * @param request The request to handle.
	 * @param response The response.
	 * @return The string to be sent as response.
	 */
	public static String getTracesByServiceName(Request request, Response response) {
    	
		response.type("application/json");
        
        String serviceName = request.queryParams("service");
        EPOnDemandQueryResult result = null;
        
        synchronized (response) {
        	TracesAPIQueries.preparedSpansServiceName.setObject(1, serviceName);     	
        	result = EsperHandler.cepRT.executeQuery(TracesAPIQueries.preparedSpansServiceName);
        }
        

		if (result != null) 
            if (result.getArray().length > 0) {
            	StringBuilder sb = new StringBuilder();
            	sb.append("{ \"spans\":[");
            	
            	for (EventBean e : result.getArray()) {
            		sb.append(" ");
            		sb.append(EventToJsonConverter.spanFromEB(e));
            		sb.append(",");
            	}
            		
            	sb.deleteCharAt(sb.length() - 1);
            	sb.append("] }");
            	
            	return sb.toString();
            } 
        
        return "{ \"spans\":[]}";

	}
	
	/**
	 * Static method to handle {@code GET /api/logs/:key}.
	 * @param request The request to handle.
	 * @param response The response.
	 * @return The string to be sent as response.
	 */
	public static String getLogsByKey(Request request, Response response) {
        
		String key = request.params(":key");
		EPOnDemandQueryResult result = null;

		TracesAPIQueries.preparedLogs.setObject(1, key);
        	
    	result = EsperHandler.cepRT.executeQuery(TracesAPIQueries.preparedLogs);
		
		StringBuilder sb = new StringBuilder();
		for (EventBean e : result.getArray())
			sb.append(e.getUnderlying() + "\n");
        
		response.type("application/json");
        
		return "{ \"result\" : \"" + sb.toString() + "\"}";

	}
	
	/**
	 * Static method to handle {@code GET /api/dependencies/:traceId}.
	 * @param request The request to handle.
	 * @param response The response.
	 * @return The string to be sent as response.
	 */
	public static String getDependenciesByTraceId(Request request, Response response) {
		
		String traceId = request.params(":traceId");
		EPOnDemandQueryResult result = null;

		TracesAPIQueries.preparedDependencies.setObject(1, traceId);
        	
    	result = EsperHandler.cepRT.executeQuery(TracesAPIQueries.preparedDependencies);
		
    	response.type("application/json");
		if (result != null) 
            if (result.getArray().length > 0) {
            	
            	StringBuilder sb = new StringBuilder();
            	sb.append("{ \"dependencies\":[");
            	for (EventBean row : result.getArray()) {
            		sb.append(" { ");
    	    		sb.append("\"serviceFrom\" : ");
    	    		sb.append(" \"" + row.get("serviceFrom") + "\", ");
    	    		sb.append("\"serviceTo\" : ");
    	    		sb.append(" \"" + row.get("serviceTo") + "\", ");
    	    		sb.append("\"numInteractions\" : ");
    	    		sb.append(" \"" + row.get("numInteractions") + "\"");
    	    		sb.append("},");
            	}
            	sb.deleteCharAt(sb.length() - 1);
            	sb.append("] }");
            	
            	return sb.toString();
            	
            } 
        
		return "{ \"traceIDsHex\":[]}";
	}

}
