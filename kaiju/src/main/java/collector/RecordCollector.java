package collector;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utils class to save records to files. Files are saved each time the buffer is full.
 * Records are saved as {@code .csv} file, one record on each line with comma as separator char.
 */
public class RecordCollector {
	
	private final static Logger log = LoggerFactory.getLogger(RecordCollector.class);
	private final int BUFFER_SIZE;
	
	private Set<String[]> dataToWrite;
	private String filepath;
	private AtomicInteger numbRecords; 
	
	/**
	 * Constructor of a RecordCollector.
	 * @param filepath The path to the file to save records
	 * @param bufferSize The buffer size
	 */
	public RecordCollector (String filepath, int bufferSize) {	
		dataToWrite = ConcurrentHashMap.newKeySet();
		numbRecords = new AtomicInteger(0);
		this.BUFFER_SIZE = bufferSize;
		this.filepath = filepath;
		
		//Empty file
		saveData(false);
	}
	
	/**
	 * Method to add a record to the buffer.
	 * @param record Record to be added to the buffer
	 */
	public void addRecord(String[] record) {
		dataToWrite.add(record);
		numbRecords.getAndIncrement();
		
		//Save data to file
		synchronized(numbRecords) {
			if (numbRecords.get() > BUFFER_SIZE) {
				
				//System.out.println("Saving " + filepath + " " + numbRecords.get() + "records");
				numbRecords.set(0);
				saveData(true);	
				
			}
		}
	}
	
	/**
	 * Public method to save data. It is public to be called even if the buffer
	 * is not full.
	 * @param append {@code True} if records in the buffer must be appended to other saved records
	 */
	public void saveData(boolean append) {
		
        FileWriter pw;
		try {
			pw = new FileWriter(filepath, append);
        
	        synchronized (dataToWrite) {
		        Iterator<String[]> s = dataToWrite.iterator();
		        while(s.hasNext()){      	       		
		        		String[] current  = s.next();
		        		
		        		if (current.length > 0) {
		        			for (int i = 0; i < current.length - 1; i++) {
		        				String str = current[i];
		        				pw.append(str);
		        				pw.append(",");
		        			}
		        			String str = current[current.length - 1];
		        			pw.append(str);
		                    pw.append("\n");            
		            	}
		        		pw.flush();
		        		dataToWrite.remove(current);		
		        }
		        pw.close();
	        }
	        
        } catch (IOException e) {
			log.error("Error in saving records to file: " + e.getMessage());
		}
            
    }
	
	
	
}
