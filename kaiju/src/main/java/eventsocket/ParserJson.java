package eventsocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.wnameless.json.flattener.JsonFlattener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.lang.Exception;
import java.util.Map;

import eps.EsperHandler;

/**
 * Runnable class parsing {@link eventSocket.Metric Metric}, {@link eventSocket.Flog FLog} and
 * {@link eventSocket.Event Event} events from JSON format to Java objects.
 */
public class ParserJson implements Runnable {
	
	private final static Logger log = LoggerFactory.getLogger(ParserJson.class);
	private String jsonToParse;
	
	/**
	 * Constructor of the ParserJson class.
	 * @param readLine The JSON {@code String} to be parsed.
	 */
	public ParserJson(String readLine) {
		jsonToParse = readLine;
	}
	
	/**
	 * Runnable implementation parsing the JSON {@code String}.
	 */
	@Override
	public void run() {
		
		Gson gson = new Gson();
		
		JsonParser parser = new JsonParser();
		
		try {
			
			JsonObject jObj = (JsonObject) parser.parse(jsonToParse);	
			
			//METRICS
			if (jObj.get("name") != null){
				EsperHandler.sendMetric(gson.fromJson(jsonToParse, Metric.class));
				return;
			}
			
			if (jObj.get("metrics") != null){
				Metric[] metricsParsed = gson.fromJson(jObj.get("metrics"), Metric[].class);
				for (int i = 0; i < metricsParsed.length; i++)
					EsperHandler.sendMetric((Metric) metricsParsed[i]);
				return;
			}
			
			//EVENTS
			if (jObj.get("payload") != null){
				EsperHandler.sendEvent(gson.fromJson(jsonToParse, Event.class));
				return;
			}
				
			if(jObj.get("events") != null) {
				Event[] eventsParsed = gson.fromJson(jObj.get("events"), Event[].class);
				for (int i = 0; i < eventsParsed.length; i++)
					EsperHandler.sendEvent((Event) eventsParsed[i]);
				return;
			}
			
			Map<String, Object> flattenJson = JsonFlattener.flattenAsMap(jsonToParse);
			
			FLog l = new FLog(flattenJson);
			EsperHandler.sendFLog(l);
					
				
		} catch (Exception e){
			log.info("Error parsing json " + jsonToParse + " | LINE DISCARDED");
			log.info(e.getClass().getSimpleName());
			log.info(e.getMessage());			
		}

	}

}
