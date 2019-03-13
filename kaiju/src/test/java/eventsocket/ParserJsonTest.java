package eventsocket;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.google.gson.Gson;

/**
 * Simple class to test {@link eventsocket.ParserJson}.
 * @author Mario
 *
 */
public class ParserJsonTest {
	
	@Test
	public void parserTest() throws InterruptedException {
		
		String s1 = "{\n" + 
				"    \"fields\": {\n" + 
				"        \"field_1\": 30,\n" + 
				"        \"field_2\": 4,\n" + 
				"        \"field_N\": 59,\n" + 
				"        \"n_images\": 660\n" + 
				"    },\n" + 
				"    \"name\": \"docker\",\n" + 
				"    \"tags\": {\n" + 
				"        \"host\": \"raynor\"\n" + 
				"    },\n" + 
				"    \"timestamp\": 1458229140\n" + 
				"}";
		
		String s2 = " {\n" + 
				"    \"metrics\": [\n" + 
				"        {\n" + 
				"            \"fields\": {\n" + 
				"                \"field_1\": 30,\n" + 
				"                \"field_2\": 4,\n" + 
				"                \"field_N\": 59,\n" + 
				"                \"n_images\": 660\n" + 
				"            },\n" + 
				"            \"name\": \"docker\",\n" + 
				"            \"tags\": {\n" + 
				"                \"host\": \"raynor\"\n" + 
				"            },\n" + 
				"            \"timestamp\": 1458229140\n" + 
				"        },\n" + 
				"        {\n" + 
				"            \"fields\": {\n" + 
				"                \"field_1\": 30,\n" + 
				"                \"field_2\": 4,\n" + 
				"                \"field_N\": 59,\n" + 
				"                \"n_images\": 660\n" + 
				"            },\n" + 
				"            \"name\": \"docker\",\n" + 
				"            \"tags\": {\n" + 
				"                \"host\": \"raynor\"\n" + 
				"            },\n" + 
				"            \"timestamp\": 1458229140\n" + 
				"        }\n" + 
				"    ]\n" + 
				"}";
		
		String s3 = " {\n" + 
				"    \"events\": [\n" + 
				"        {\n" + 
				"           \"timestamp\": 1458229140,\n" + 
				"            \"payload\": {\n" +  
				"                \"commit_msg\": \"Fix connection pool\"\n" + 		
				"            },\n" + 
				"            \"context\": {\n" +
				"                \"commit_id\" : \"de9c1a087f47605cd7e33a585ee34d628a4a49b4\"\n" +			
				"            }\n" + 
				"		},\n" +
				"      {\n" + 
				"           \"timestamp\": 1458249140,\n" +
				"            \"payload\": {\n" +  
				"                \"alert_msg\": \"HighCPUUsage\",\n" + 
				"				 \"percentage_cpu\": \"80\"\n" +				
				"            },\n" + 
				"            \"context\": {\n" +
				"                \"alert_name\" : \"HighCPUUsage\"\n" +			
				"            }\n" + 
				"      }\n" + 
				"    ]\n" + 
				"}";
		
		String s4 = "{" + 
				"\"timestamp\": 1458229140," + 
				"\"payload\": {" +  
				"\"commit_msg\": \"Fix connection pool\"" + 		
				"}," + 
				"\"context\": {" +
				"\"commit_id\" : \"de9c1a087f47605cd7e33a585ee34d628a4a49b4\"" +			
				"}" + 
				"}";
		
		Gson gson = new Gson();
		assertNotNull(gson.fromJson(s1, Metric.class));
		assertNotNull(gson.fromJson(s2, Metric.class));
		assertNotNull(gson.fromJson(s3, Event.class));
		assertNotNull(gson.fromJson(s4, Event.class));

	}

}
