package api.traces;

import spark.Service;

/**
 * Class to manage the KaijuAPI.
 *
 */
public class TracesAPI {
	
	/**
	 * Static method to initialize the API on port {@code 9278}. <ul>
	 * <li> {@code POST /api/query?query=<query>} Execute the fire and forget query sent.
	 * <li> {@code POST /api/statement?statement=<statement>&msg=<msg>} Install the given statement with a {@link eps.listener.CEPListener}
	 * registered with the given message. Returns the statement code.
	 * <li> {@code POST /api/remove?statement=<stmt_code>} Remove the statement with the given code.
	 * <li> {@code GET /api/traces/all} Return all {@code traceId}s related to spans currently retained by the Esper Engine.
	 * <li> {@code GET /api/traces?service=<service>} Return all spans for a given {@code serviceName}
	 * currently retained by the Esper Engine.
	 * <li> {@code GET /api/traces/:id} Return all spans for a given {@code traceId} currently retained by the Esper Engine.
	 * <li> {@code GET /api/logs/:key} Return a tuple {@code (spanId, operationName, logs)} for all logs with a field with the given 
	 * {@code key} currently retained by the Esper Engine.
	 * <li> {@code GET /api/dependencies/:traceId} Return a set of tuples {@code (serviceFrom, serviceTo, numInteractions)} representing interactions
	 * between services in a given trace.
	 * </ul>
	 * All requests return {@code status 400} and an error message if Kaiju reports an error while managing the request.
	 */
    public static void initAPI() {
    	
    	TracesAPIQueries.initPreparedQueries();
    	
    	Service http = Service.ignite()
    			.port(9278);
    	
    	// EXCEPTIONS
		http.exception(Exception.class, (e, request, response) -> {
			response.type("application/json");
			response.status(400);
			response.body("{ \"exception\":\"" + e.getClass() + "\","
					+ "\"message\":\"" + e.getMessage() + "\""
					+ "}");
		});
		
		// POST /api/query?query=<query>
    	http.post("/api/query", (request, response) -> TracesAPIHandler.fireQuery(request, response));
    	
		// POST /api/statement?statement=<statement>&msg=<msg>
    	http.post("/api/statement", (request, response) -> TracesAPIHandler.registerStatement(request, response));
    	
    	// POST /api/remove?statement=<stmt_code>
    	http.post("/api/remove", (request, response) -> TracesAPIHandler.removeStatement(request, response));
    	
    	// GET /api/traces/all
    	http.get("/api/traces/all", (request, response) -> TracesAPIHandler.getAllTracesIds(request, response));
    	
    	// GET /api/traces?service=<service>
        http.get("/api/traces", (request, response) -> TracesAPIHandler.getTracesByServiceName(request, response));
        
        // GET /api/traces/:id
        http.get("/api/traces/:id", (request, response) -> TracesAPIHandler.getTraceByTraceId(request, response));
        
        // GET /api/logs/:key
        http.get("/api/logs/:key", (request, response) -> TracesAPIHandler.getLogsByKey(request, response));
        
        // GET /api/dependencies/:traceId
        http.get("/api/dependencies/:traceId", (request, response) -> TracesAPIHandler.getDependenciesByTraceId(request, response));

    }

}
