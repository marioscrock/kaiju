package collector;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import thriftgen.Batch;

/** 
 * Class offering a static method to serialize batches in a JSON file.
 * Static fields:<ul>
 * <li> {@code int minNumBatchToSerialize}	default value is 0
 * <li> {@code filepath String}	default value is {@code "./dumpTraces.json"}
 * </ul>
 */
public class BatchSerialize {
	
	private final static Logger log = LoggerFactory.getLogger(BatchSerialize.class);
	public static int minNumBatchToSerialize;
	public static String filepath = "./dumpTraces.json";
	
	private static List<String> strings = Collections.synchronizedList(new ArrayList<String>());	
	
	/**
	 * Static method to serialize a batch. If {@code numbBatches} is higher than field {@code minNumBatchToSerialize}
	 * it writes batches to file specified by the field {@code filepath}.
	 * @param batch The batch to serialize
	 * @param numbBatches Batch number
	 */
	public static void serialize(Batch batch, int numbBatches) {
		
		Gson gson = new Gson();
		
		if (numbBatches <= minNumBatchToSerialize) {
			
			synchronized (strings) {
				strings.add(gson.toJson(batch));
			}
		
		} else { 
			
	        PrintWriter file;
			try {
				
				file = new PrintWriter (new FileWriter(filepath));
				
				StringBuilder b = new StringBuilder();
				b.append("[");
				
				synchronized (strings) {
					for (String s : strings) {
						b.append(s);
						b.append(",");
					}
				}
				
				b.deleteCharAt(b.toString().lastIndexOf(","));
				b.append("]");
				
				file.append(b.toString());
	            file.close();
	            
			} catch (IOException e) {
				log.error("Error in serializing batches to file: " + e.getMessage());
			}
		}
			
	}

}
