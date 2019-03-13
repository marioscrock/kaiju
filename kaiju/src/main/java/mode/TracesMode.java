package mode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.uber.tchannel.api.SubChannel;
import com.uber.tchannel.api.TChannel;

import api.traces.TracesAPI;
import collector.CollectorHandler;
import eps.EsperHandler;
import eps.EsperStatements;
import eps.utils.StatementParser;
import eventsocket.Event;
import thriftgen.Batch;
import thriftgen.Log;
import thriftgen.Process;
import thriftgen.Span;
import thriftgen.SpanRef;
import thriftgen.Tag;

/**
 * Class implementing Mode interface for Traces mode.
 *
 */
public class TracesMode implements Mode {
	
	private final static Logger log = LoggerFactory.getLogger(TracesMode.class);
	
	private boolean api;
	protected CollectorHandler ch;
	
	public TracesMode(boolean api) {
		this.api = api;
	}

	@Override
	public void init() throws InterruptedException {
		
		EsperHandler.initializeHandler();
		ch = new CollectorHandler();
		
		//Create TChannel to serve jaeger-agents
		TChannel tchannel = new TChannel.Builder("kaiju-collector")
				.setServerPort(2042)
				.build();
		
		ch.setThriftTiming(true);
		
		//Register Handler for submitBatch interface defined in thrift file
		SubChannel subCh = tchannel.makeSubChannel("kaiju-collector");
		subCh.register("Collector::submitBatches", ch);
		log.info("Handler registered for Collector::submitBatches");
		
		// listen for incoming connections
		tchannel.listen().channel().closeFuture().sync(); //tchannel.listen()
        tchannel.shutdown();

	}
	
	@Override
	public void addEventTypes(Configuration cepConfig) {
	    cepConfig.addEventType("Batch", Batch.class.getName());
	    cepConfig.addEventType("Span", Span.class.getName());
	    cepConfig.addEventType("Process", Process.class.getName());
	    cepConfig.addEventType("Log", Log.class.getName());
	    cepConfig.addEventType("Tag", Tag.class.getName());
	    cepConfig.addEventType("SpanRef", SpanRef.class.getName());
	   
	    cepConfig.addEventType("Event", Event.class.getName());	
	}

	@Override
	public void addStatements(EPAdministrator cepAdm, boolean parse) {		
		EsperStatements.defaultStatementsTraces(cepAdm, EsperHandler.RETENTION_TIME);
		if (parse)
			StatementParser.parseStatements(cepAdm, "./stmts/statements.txt", EsperHandler.RETENTION_TIME);
		
		if (api) {		
			Thread APIThread = new Thread(new Runnable() {
				@Override
				public void run() {
					TracesAPI.initAPI();	
				}			
			});
	        APIThread.run();
		}
	}
	
	@Override
	public String toString() {
		return "Traces Mode";
	}

}
