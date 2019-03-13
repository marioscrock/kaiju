package eps.utils;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.soda.*;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parse {@code Event}s from file installing {@code create schema} and {@code insert into} statements accordingly.
 * Syntax: event-name>{payload-key:type[,payload-key:type]*}>{context-key:type[,context-key:type]*}>{inherits-event-name[,inherits-event-name]*}
 */
public class EPLFactory {
	
	private final static Logger log = LoggerFactory.getLogger(EPLFactory.class);

    private static String toEPLInsertInto(String into, Map<String, String> p_fields, Map<String, String> c_fields) {
    	
        EPStatementObjectModel stmt = new EPStatementObjectModel();
        InsertIntoClause insertIntoClause = InsertIntoClause.create(into);
        stmt.insertInto(insertIntoClause);
        
        SelectClause selectClause = SelectClause.create();
        
        //TIMESTAMP
        selectClause.addWithAsProvidedName("timestamp", "timestamp");
        
        //PAYLOAD
    	for(String pk : p_fields.keySet()) {
    		PropertyValueExpression p = new PropertyValueExpression("payload('" + pk + "')");
    		CastExpression ce = new CastExpression(p, p_fields.get(pk));
    		selectClause.add(ce, pk);
    	}
              
        //CONTEXT
    	for(String ck : c_fields.keySet()) {
    		PropertyValueExpression p = new PropertyValueExpression("context('" + ck + "')");
    		CastExpression ce = new CastExpression(p, c_fields.get(ck));
    		selectClause.add(ce, ck);
    	}
        
        stmt.setSelectClause(selectClause);
        FromClause fromClause = FromClause.create();
        FilterStream stream = FilterStream.create("Event");
        fromClause.add(stream);
        stmt.setFromClause(fromClause);
        
        if (!p_fields.isEmpty()) {
        	Conjunction where = Expressions.and();
	        for(String f : p_fields.keySet()) {
	    		String b = "payload.containsKey('" + f + "')";
	    		where.add(b);
	    	}
	        stmt.setWhereClause(where);
        }
        
        log.info(stmt.toEPL());
        return stmt.toEPL();
    }

    private static String toEPLSchema(String name, Map<String, String> fields, List<String> inherits) {
        CreateSchemaClause schema = new CreateSchemaClause();
        schema.setSchemaName(name);
        
        if (inherits != null) {
        	schema.setInherits(new HashSet<>(inherits));
        }
        
        List<SchemaColumnDesc> columns = new ArrayList<>();        
        for(String f : fields.keySet()) {
        	columns.add(new SchemaColumnDesc(f, fields.get(f), false));
        }        
        schema.setColumns(columns);        
        
        StringWriter writer = new StringWriter();
        schema.toEPL(writer);
        System.out.println(writer.toString());
        return writer.toString();
    }


    public static void parseHLEvents(EPAdministrator cepAdm, String filepath) {
    	
    	//All events created inherit from HLEvent
    	cepAdm.createEPL("create schema HLEvent as (timestamp long)");
    	
    	List<String> listEvents = parseFile(filepath);	
    	if (listEvents != null) 
    		for (String e : listEvents)
    			createSchemaAndInsertInto(e, cepAdm);
    	
    }

	private static void createSchemaAndInsertInto(String e, EPAdministrator cepAdm) {
		
		String e_name = "";
		Map<String, String> p_fields = new HashMap<>();
		Map<String, String> c_fields = new HashMap<>();
		List<String> inherits = new ArrayList<>();
		inherits.add("HLEvent");
		
		try {		
			String[] e_array = e.split(">");
			e_name = e_array[0];
			
			if (e_array.length > 1 && !e_array[1].equals("")) {
				String[] e_fields_types = e_array[1].substring(1, e_array[1].length()-1).split(",");
				for (String ft : e_fields_types) {			
					//name:type
					p_fields.put(ft.split(":")[0], ft.split(":")[1]);
				}
			}
			
			if (e_array.length > 2 && !e_array[2].equals("")) {
				String[] e_fields_types = e_array[2].substring(1, e_array[2].length()-1).split(",");
				for (String ft : e_fields_types) {			
					//name:type
					c_fields.put(ft.split(":")[0], ft.split(":")[1]);
				}
			}
			
			if (e_array.length > 3 && !e_array[3].equals("") ) {
				inherits.addAll(Arrays.asList(e_array[3].substring(1, e_array[3].length()-1).split(",")));
			}
			
			Map<String, String> fields = new HashMap<String, String>(p_fields);
			fields.putAll(c_fields);
			
			//CREATE
			try {
				cepAdm.createEPL(toEPLSchema(e_name, fields, inherits));
			} catch (Exception eEPL) {
				log.error("Error parsing EPL stmt: " + eEPL.getClass().getSimpleName() + " " + eEPL.getMessage());
			}
			
			//INSERT INTO
			if (!p_fields.isEmpty()) {
				try {
					cepAdm.createEPL(toEPLInsertInto(e_name, p_fields, c_fields));
				} catch (Exception eEPL) {
					log.error("Error parsing EPL stmt: " + eEPL.getClass().getSimpleName() + " " + eEPL.getMessage());
				}
			}
			
		} catch (Exception eVal) {
			log.error("Failed validating events file: " + eVal.getClass().getSimpleName() + " " + eVal.getMessage());
		}
		
	}

	private static List<String> parseFile(String filepath) {
		
		List<String> listEvents = new ArrayList<String>();
		
		try (Stream<String> lines = Files.lines(Paths.get(filepath))) {
			   listEvents = lines.collect(Collectors.toList());
		} catch (IOException e) {
			log.error("Error in reading events schemas from file: " + e.getMessage());
			return null;
		}
		
		return listEvents;
	}
}
