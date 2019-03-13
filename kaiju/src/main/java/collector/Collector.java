package collector;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

import eps.EsperHandler;
import mode.HLMode;
import mode.LogsMode;
import mode.MetricsMode;
import mode.Mode;
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

	public static void main(String... argv) throws InterruptedException {
		
		Config config = new Config();
        JCommander.newBuilder()
            .addObject(config)
            .build()
            .parse(argv);
		
		if (getMode(config) == null) {
			log.error("Specify mode to instantiate the Kaiju instance");
			return;
		}
		log.info("Esper mode set: " + EsperHandler.MODE.toString());
		log.info("Esper retention time set: " + config.retentionTime);
		log.info("Parse boolean set: " + config.parse);
		
		//Set parsed config to Esper
		EsperHandler.config = config;

    	//Executors to handle incoming requests
    	BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
    	executor = new ThreadPoolExecutor(3, 5,
    			10000, TimeUnit.MILLISECONDS, workQueue);
    	log.info("Executors pool initialised");
		
    	EsperHandler.MODE.init();
		
	}
	
	private static Mode getMode(Config config) {
		
		switch (config.mode) {
		case "metrics":
			return EsperHandler.MODE = new MetricsMode();
		case "logs":
			return EsperHandler.MODE = new LogsMode();
		case "traces":
			return EsperHandler.MODE = new TracesMode(false);
		case "traces-api":
			return EsperHandler.MODE = new TracesMode(true);
		case "high-level":
			return EsperHandler.MODE = new HLMode();
		default:
			return null;
		}
		
	}

}
