package collector;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eps.EsperHandler;
import mode.HLMode;
import mode.LogsMode;
import mode.MetricsMode;
import mode.TracesMode;

/**
 * Main class to launch the Kaiju instance.
 * args[0] sets the mode for Kaiju: traces (or traces-api), logs, metrics or high-level (default is "traces").
 * args[1] sets the retention time for Esper windows (default is 2 min). Can be specified in parsed statements
 * with the :retentionTime: placeholder.
 */
public class Collector {
	
	private final static Logger log = LoggerFactory.getLogger(Collector.class);
	
	public static ThreadPoolExecutor executor;	

	public static void main(String[] args) throws InterruptedException {
		
		//SET MODE Esper		
		if(args.length < 1) {
			log.error("Specify mode to instantiate the Kaiju instance");
			return;
		}
		
		switch (args[0]) {
		case "metrics":
			EsperHandler.MODE = new MetricsMode();
			break;
		case "logs":
			EsperHandler.MODE = new LogsMode();
			break;
		case "traces-api":
			EsperHandler.MODE = new TracesMode(true);
			break;
		case "high-level":
			EsperHandler.MODE = new HLMode();
			break;
		default:
			EsperHandler.MODE = new TracesMode(false); //Default
			break;
		}
		log.info("Esper mode set: " + EsperHandler.MODE.toString());
		
		//Set retention time Esper
		if(args.length > 1)
			EsperHandler.RETENTION_TIME = args[1];
		log.info("Esper retention time set: " + EsperHandler.RETENTION_TIME);

    	//Executors to handle incoming requests
    	BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
    	executor = new ThreadPoolExecutor(3, 5,
    			10000, TimeUnit.MILLISECONDS, workQueue);
    	log.info("Executors pool initialised");
		
    	EsperHandler.MODE.init();
		
	}

}
