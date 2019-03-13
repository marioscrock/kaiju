package mode;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;

import eps.EsperHandler;
import eps.utils.EPLFactory;
import eps.utils.StatementParser;
import eventsocket.Event;

/**
 * Class implementing Mode interface for High-Level mode.
 *
 */
public class HLMode implements Mode {
	
	@Override
	public void addEventTypes(Configuration cepConfig) {		
	    cepConfig.addEventType("Event", Event.class.getName());		
	}

	@Override
	public void addStatements(EPAdministrator cepAdm, boolean parse) {
		
		EPLFactory.parseHLEvents(cepAdm, "./stmts/events.txt");
		
		if (parse)
			StatementParser.parseStatements(cepAdm, "./stmts/statements.txt", EsperHandler.RETENTION_TIME);
				
	}
	
	@Override
	public String toString() {
		return "High-level mode";
	}

}
