package collector;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.TException;

import com.google.gson.Gson;

import eps.EsperHandler;
import mode.TracesMode;
import thriftgen.Batch;

/**
 * Main class to launch a fake kaiju-collector instance. It emulates incoming batches 
 * reading them from a JSON file.
 */
public class FakeCollector extends TracesMode {
	
	private final static String FILEPATH = "/dumpTraces.json";
	private static int sentBatches;
	private static List<Batch> batches;
	
	public FakeCollector(boolean api) {
		super(api);
	}
	
	public void main(String[] args) throws InterruptedException {
		FakeCollector fc = new FakeCollector(false);
		fc.exec();
	}
	
	//It schedules at fixed rate incoming batches reading them from a JSON file.
	public void exec() throws InterruptedException {
		
		EsperHandler.MODE = this;
		
		sentBatches = 0;
		batches = new ArrayList<>();
    	
		//Read batches from file
		Gson gson = new Gson();
		InputStream in = FakeCollector.class.getResourceAsStream(FILEPATH);
		Batch[] batchesArray = gson.fromJson(new BufferedReader(new InputStreamReader(in)), Batch[].class);
		batches = Arrays.asList(batchesArray);
		
		EsperHandler.MODE.init();
		
		//Schedule batches
		final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	    executorService.scheduleAtFixedRate(this::sendBatch, 0, 500, TimeUnit.MILLISECONDS);
	  
		return;

	}
	
	/**
	 * Send a batch to the handler. Unit of work for the executor.
	 */
	private void sendBatch() {
	    
		if (sentBatches < batches.size()) {
			List<Batch> batchesToSend = new ArrayList<Batch>();
			batchesToSend.add(batches.get(sentBatches));
			try {
				ch.submitBatches(batchesToSend);
			} catch (TException e) {
				e.printStackTrace();
			}	
			sentBatches += 1;
		}
	}



}
