package com.ms.consume.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ms.consume.api.controller.services.FilterSchemaServiceImpl;
import com.ms.consume.api.controller.services.LoadBigQueryServiceImpl;
import com.ms.consume.api.controller.services.UploadBucketServiceImpl;
import com.ms.consume.api.models.RequestAPIConsume;

@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS,
		RequestMethod.HEAD })
@RestController
@RequestMapping( value = {"/api"})
public class ConsumeAPIController {
	
	private static Logger log = LoggerFactory.getLogger(ConsumeAPIController.class);
	private RestTemplate restTemplate = new RestTemplate();
	
	@Autowired
	FilterSchemaServiceImpl filterSchemaServiceImpl;
	
	@Autowired
	UploadBucketServiceImpl uploadBucketServiceImpl;
	
	@Autowired
	LoadBigQueryServiceImpl loadBigQueryServiceImpl;

	@Autowired
	public ConsumeAPIController(RestTemplate resTemplare) {
		this.restTemplate = restTemplate;
	}
	
	@GetMapping("/test")
	public String consume() {
		
		String response = "TEST";
		return response;
		
	}

	@GetMapping("/consume")
	public String consumeAPI(@RequestBody String request) {

		Object response = null;
		JSONObject jsonBody = null;
		String jsonResultToBucket = "";
		String csv = "";
		Object responseFilter = "";
		Object uploadBucket = "";
		Object loadBigQuery = "";
		
		
		String client_id = "";
		String client_password = "";
		String token = "";
		String username = "";
		String password = "";
		
		try {			
			String rqBody = request.toString();
			JSONParser parser = new JSONParser();
	        //Object jsonrow = parser.parse(rqBody);
			JSONObject jsonrow = (JSONObject) parser.parse(String.valueOf(rqBody));

			// CREDENTIAL AUTHORIZATION			
			client_id = (String) jsonrow.get("client_id");
			client_password = (String) jsonrow.get("client_password");
			token = (String) jsonrow.get("access_token");
			
			username = (String) jsonrow.get("username");
			password = (String) jsonrow.get("password");
			
			HttpHeaders headers = new HttpHeaders();
			headers.set("X-Shopify-Access-Token", token);
			headers.setBasicAuth(username, password);
			HttpEntity requestEntity = new HttpEntity(headers);

			Map<String, String> vars = new HashMap<>();
			vars.put("client_id", client_id);
			vars.put("client_password", client_password);
			////////////////////////////////////////////////////////////////////////////////////////////////
			
			// ENDPOINT
			String url = (String) jsonrow.get("url");
			String body = (String) jsonrow.get("body");
			////////////////////////////////////////////////////////////////////////////////////////////////
			
			// SCHEMA
			String enableBucket = (String) jsonrow.get("enableBucket");
			String enableBigQuery = (String) jsonrow.get("enableBigQuery");
			String nameFile = (String) jsonrow.get("nameFile");
			String nameBucket = (String) jsonrow.get("nameBucket");
			String nameDataset = (String) jsonrow.get("nameDataset");
			String nameTable = (String) jsonrow.get("nameTable");
			JSONObject schema = (JSONObject) jsonrow.get("schema");		
			Object list = schema.get("list");
			JSONArray filter = (JSONArray) schema.get("filter"); 
			////////////////////////////////////////////////////////////////////////////////////////////////
			
			// Consume API External
			response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Object.class, vars);
			
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String responseJson = ow.writeValueAsString(response);
			//System.out.println("responseJson: " + responseJson);
			
			JSONParser parser_ = new JSONParser();
			JSONObject jsonO = (JSONObject) parser_.parse(responseJson);

			jsonBody = new JSONObject();
			jsonBody = (JSONObject) jsonO.get("body");
			////////////////////////////////////////////////////////////////////////////////////////////////
			
			//parse(jsonBody.toString());
			
//			com.google.gson.JsonObject object = (JsonObject) parser.parse( jsonBody.toString() );
//
//	        Set <java.util.Map.Entry<String, com.google.gson.JsonElement>> keys = object.entrySet();
//	        if ( keys.isEmpty() ) {
//	            System.out.println( "Empty JSON Object" );
//	        }else {
//	            Map<String, Object> map = json_UnKnown_Format( keys );
//	            System.out.println("Json 2 Map : "+map);
//	        }
			
			// Filter API
			responseFilter = filterSchemaServiceImpl.filterSchema(list, filter, jsonBody);
			// Upload Bucket
			if(enableBucket.equals("ON")) {
				uploadBucket = uploadBucketServiceImpl.uploadBucket(nameFile, nameBucket, responseFilter.toString());
			}
			
			// load BigQuery
			if(enableBigQuery.equals("ON")) {
				loadBigQuery = loadBigQueryServiceImpl.loadBigQuery(filter, nameFile, nameBucket, nameDataset, nameTable);
			}
			
		} catch (Exception e) {
			log.debug("Exception {}", e.toString());
			e.printStackTrace();
		}

		return responseFilter.toString();

	}
	
	public void parse(String json) throws JsonMappingException, JsonProcessingException  {
	       JsonFactory factory = new JsonFactory();

	       ObjectMapper mapper = new ObjectMapper(factory);
	       JsonNode rootNode = mapper.readTree(json);  
	       
//	       Map.Entry<String,JsonNode> field_ = null;
//	       Map.Entry<String,JsonNode> field__ = null;

	       Iterator<Map.Entry<String,JsonNode>> fieldsIterator = rootNode.fields();
	       while (fieldsIterator.hasNext()) {
	    	   	    	   
	           Map.Entry<String,JsonNode> field = fieldsIterator.next();
	           
	           if(field.getValue().size() > 0) {
	        	   System.out.println("Mayor");
	        	   System.out.println("Key: " + field.getKey() + "\tValue:" + field.getValue());
	        	   JsonFactory factory_ = new JsonFactory();

	    	       ObjectMapper mapper_ = new ObjectMapper(factory_);
	    	       JsonNode rootNode_ = mapper_.readTree(field.getValue().toString()); 
	    	       if (rootNode_.isArray()) {
	    	    	    for (final JsonNode objNode : rootNode_) {
	    	    	        System.out.println(objNode);
	    	    	    }
	    	    	}
//	        	   ArrayNode fieldsIterator_ = (ArrayNode) rootNode_.fields();
//	        	   for (int i = 0; i < fieldsIterator_.size(); i++) {
//	        		   System.out.println(fieldsIterator_.get(i));
//	        	   }
    	   	    	   
//	    	           Map.Entry<String,JsonNode> field_ = fieldsIterator_.next();
//	    	           if(field_.getValue().size() > 0) {
//	    	        	   System.out.println("Mayor_");
//	    	        	   System.out.println("Key: " + field_.getKey() + "\tValue:" + field_.getValue());
//	    	        	   System.out.println("Mayor_");
//	    	           }else {
//	    	        	   System.out.println("Menor_");
//	    	        	   System.out.println("Key: " + field_.getKey() + "\tValue:" + field_.getValue());
//	    	        	   System.out.println("Menor_");
//	    	           }
//	        	   }
	        	   
	        	   System.out.println("Mayor");
	           }else {
	        	   System.out.println("Menor");
	        	   System.out.println("Key: " + field.getKey() + "\tValue:" + field.getValue());
	        	   System.out.println("Menor");
	           }
	           
	           
//	           if(field.getValue().size() > 0) {
//	        	   for (int i = 0; i < field.getValue().size(); i++) {
//	        		   JsonFactory factory_ = new JsonFactory();
//	        	       ObjectMapper mapper_ = new ObjectMapper(factory_);
//	        	       JsonNode rootNode_ = mapper_.readTree(field.getValue().get(i).toString()); 
//	        	       Iterator<Map.Entry<String,JsonNode>> fieldsIterator_ = rootNode_.fields();
//	        		   
//	        		   while(fieldsIterator_.hasNext()) {
//	        			   field_ = fieldsIterator_.next();
//	        	           System.out.println("Key: " + field_.getKey() + "\tValue:" + field_.getValue());
//	        	           if(field_.getValue().size() > 0) {
//	        	        	   String typeJ = field__.getClass().getTypeName();
//	        	        	   System.out.println("Type map: " + typeJ);
//	        	        	   for (int k = 0; k < field_.getValue().size(); k++) {
//	        	        		   JsonFactory factory__ = new JsonFactory();
//	        	        	       ObjectMapper mapper__ = new ObjectMapper(factory__);
//	        	        	       if(field_.getValue().get(k) != null) {	        	        	    	   	        	        	      
//		        	        	       //ArrayNode fieldC = (ArrayNode) field_.getValue().get(k);
//		        	        	       JsonNode node = mapper__.convertValue(field_.getValue().get(k), JsonNode.class);
//		        	        	       JsonNode rootNode__ = mapper__.readTree(node.textValue()); 
//		        	        	       Iterator<Map.Entry<String,JsonNode>> fieldsIterator__ = rootNode__.fields();
//		        	        	       while(fieldsIterator__.hasNext()) {
//		        	        			   field__ = fieldsIterator__.next();
//		        	        	           System.out.println("Key: " + field__.getKey() + "\tValue:" + field__.getValue());
//		        	        	       }
//	        	        	       } else {
//	        	        	    	   //System.out.println("Key: " + field__.getKey() + "\tValue:" + "null");
//	        	        	       }
//	        	        	   }
//	        	           }
//	        		   }		        	   
//	        	   }	        	   
//	           }
	       }
	}
	
	public static Map<String, Object> json_UnKnown_Format( Set <java.util.Map.Entry<String, com.google.gson.JsonElement>> keys ) throws ParseException{
	    Map<String, Object> jsonMap = new HashMap<String, Object>();
	    for (Entry<String, JsonElement> entry : keys) {
	        String keyEntry = entry.getKey();
	        System.out.println(keyEntry + " : ");
	        JsonElement valuesEntry =  entry.getValue();
	        if (valuesEntry.isJsonNull()) {
	            System.out.println(valuesEntry);
	            jsonMap.put(keyEntry, valuesEntry);
	        }else if (valuesEntry.isJsonPrimitive()) {
	            System.out.println("P - "+valuesEntry);
	            jsonMap.put(keyEntry, valuesEntry);
	        }else if (valuesEntry.isJsonArray()) {
	            JsonArray array = valuesEntry.getAsJsonArray();
	            List<Object> array2List = new ArrayList<Object>();
	            for (JsonElement jsonElements : array) {
	                System.out.println("A - "+jsonElements);
	                array2List.add(jsonElements);
	            }
	            jsonMap.put(keyEntry, array2List);
	        }else if (valuesEntry.isJsonObject()) {             
	             JSONParser parser = null;
				com.google.gson.JsonObject obj = (JsonObject) parser.parse(valuesEntry.toString());                    
	             Set <java.util.Map.Entry<String, com.google.gson.JsonElement>> obj_key = obj.entrySet();
	             jsonMap.put(keyEntry, json_UnKnown_Format(obj_key));
	        }
	    }
	    return jsonMap;
	}
}
