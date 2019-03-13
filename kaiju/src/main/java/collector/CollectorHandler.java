package collector;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.thrift.TException;

import com.uber.tchannel.api.handlers.ThriftRequestHandler;
import com.uber.tchannel.messages.ThriftRequest;
import com.uber.tchannel.messages.ThriftResponse;

import eps.EsperHandler;
import thriftgen.Batch;
import thriftgen.Collector;

/**
 * Class to handle incoming batches. It forwards batches to the {@link eps.EsperHandler EsperHandler} and: <ul>
 * <li> {@link #setThriftTiming(boolean)} to {@code true} to enable saving records of Thrift de-serialization timings
 * </ul>
 * All default values are {@code false}.
 *
 */
public class CollectorHandler extends ThriftRequestHandler<Collector.submitBatches_args, Collector.submitBatches_result> {
		
	private RecordCollector thriftTimingCollector;
	private AtomicInteger numbBatches;
	
	private boolean thriftTiming;
	
	/**
	 *  Constructor of the CollectorHandler class
	 */
	public CollectorHandler() {	
		numbBatches = new AtomicInteger(0);
	}
	
	/**
	 * Implements the thrift-defined interface {@link thriftgen.Collector Collector}.
	 * @param batches	List of batches to submit
	 */
	public void submitBatches(List<Batch> batches) throws TException {
		
		for(Batch batch : batches) {
			
			//ESPER
			//log.info("Batch to esper");
			EsperHandler.sendBatch(batch);
			
//			//SERIALIZE BATCH to JSON
//			BatchSerialize.numBatchToSerialize = 180;
//			BatchSerialize.serialize(batch, numbBatches);
					
		}
		
	}
	
	/**
	 * Deserialize the thrift request to the correspondent {@link thriftgen.Batch Batch} objects. 
	 * @param request The request to be deserialized.
	 * @return The list of deserialized {@link thriftgen.Batch Batch} objects.
	 */
	private List<Batch> deserialize(ThriftRequest<Collector.submitBatches_args> request) {
		
		String[] timing = new String[4];
		timing[1] = Long.toString(Instant.now().toEpochMilli());
		List<Batch> batches = request.getBody(Collector.submitBatches_args.class).getBatches();
		timing[2] = Long.toString(Instant.now().toEpochMilli());
		numbBatches.getAndAdd(batches.size());
		timing[0] = Integer.toString(numbBatches.get());
		
		int batchSpansNum = 0;
		if (batches != null && batches.size() > 0) {
			for (Batch b : batches)
				batchSpansNum += b.getSpansSize();
		}
		timing[3] = Integer.toString(batchSpansNum);
		thriftTimingCollector.addRecord(timing);
		
		return batches;
	}
	
	/**
	 * Method to handle requests to the thrift interface, it executes the {@link #submitBatches(List)} method
	 * through the {@link collector.Collector#executor executors} pool.
	 * @param request The request to handle
	 * @return Empty responses 
	 */
	@Override
	public ThriftResponse<Collector.submitBatches_result> handleImpl(ThriftRequest<Collector.submitBatches_args> request) {
		
		List<Batch> batches;
		if (thriftTiming)
			batches = deserialize(request);
		else
			batches = request.getBody(Collector.submitBatches_args.class).getBatches();
		
		collector.Collector.executor.execute(new Runnable() {
				
			@Override
			public void run() {
				try {
					submitBatches(batches);
				} catch (TException e) {
					e.printStackTrace();
				}		
			}
			
		});
		
		return new ThriftResponse.Builder<Collector.submitBatches_result>(request)
	            .setBody(new Collector.submitBatches_result())
	            .build();
		
	}

	/**
	 * Get method for the thriftTiming flag
	 * @return the thriftTiming flag
	 */
	public boolean isThriftTiming() {
		return thriftTiming;
	}

	/**
	 * Set method for the thriftTiming flag. If {@code true}
	 * enables saving records of Thrift de-serialization timings.
	 * @param thriftTiming the thriftTiming to set
	 */
	public void setThriftTiming(boolean thriftTiming) {
		if (thriftTiming)
			thriftTimingCollector = new RecordCollector("./thriftTiming.csv", 200);	
		this.thriftTiming = thriftTiming;
	}

}
