package mode;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;

import eps.EsperHandler;
import eps.EsperStatements;
import eps.utils.StatementParser;
import eventsocket.Event;
import eventsocket.FLog;

/**
 * Class implementing Mode interface for Logs mode.
 *
 */
public class LogsMode implements Mode {
	
	@Override
	public void addEventTypes(Configuration cepConfig) {
	    cepConfig.addEventType("Event", Event.class.getName());
	    cepConfig.addEventType("FLog", FLog.class.getName());	
	}

	@Override
	public void addStatements(EPAdministrator cepAdm, boolean parse) {
		EsperStatements.defaultStatementsLogs(cepAdm, EsperHandler.RETENTION_TIME);
		if (parse)
			StatementParser.parseStatements(cepAdm, "./stmts/statements.txt", EsperHandler.RETENTION_TIME);
	}
	
	@Override
	public String toString() {
		return "Logs mode";
	}

}
