package com.ms.consume.api.controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

//import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
//import org.json.simple.JSONValue;
//import org.json.simple.parser.JSONParser;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonObject;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

//import net.minidev.json.JSONArray;

//import net.minidev.json.JSONArray;
//import net.minidev.json.parser.JSONParser;

@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS,
		RequestMethod.HEAD })
@RestController
@RequestMapping({ "/api/shopify" })
public class ShopifyController {

	private static Logger log = LoggerFactory.getLogger(ZendeskController.class);

	private RestTemplate restTemplate = new RestTemplate();

	@Autowired
	public ShopifyController(RestTemplate resTemplare) {
		this.restTemplate = restTemplate;
	}

	@GetMapping("/listProducts")
	public Object listProducts(@RequestBody String request) {

		Object response = null;
		JSONObject json__ = null;
		String jsonResultToBucket = "";

		try {
//			Gson shopify = new Gson();			
//			Properties properties = shopify.fromJson(request, Properties.class);
			
			JSONParser parser = new JSONParser();
			JSONObject jsonrow = (JSONObject) parser.parse(String.valueOf(request));

			// CREDENTIAL AUTHORIZATION			
			String client_id = (String) jsonrow.get("client_id");
			String client_password = (String) jsonrow.get("client_password");
			String token = (String) jsonrow.get("access_token");
			
			HttpHeaders headers = new HttpHeaders();
			headers.set("X-Shopify-Access-Token", token);
			HttpEntity requestEntity = new HttpEntity(headers);

			Map<String, String> vars = new HashMap<>();
			vars.put("client_id", client_id);
			vars.put("client_password", client_password);
			////////////////////////////////////////////////////////////////////////////////////////////////
			
			// ENDPOINT
			String url = (String) jsonrow.get("url");
			String body = (String) jsonrow.get("body");
			
			// SCHEMA
			JSONObject schema = (JSONObject) jsonrow.get("schema");		
			Object list = schema.get("list");
			JSONArray filter = (JSONArray) schema.get("filter"); 

			response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Object.class, vars);

			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(response);
			
			JSONParser parser_ = new JSONParser();
			JSONObject jsonO = (JSONObject) parser_.parse(json);

			json__ = new JSONObject();
			json__ = (JSONObject) jsonO.get("body");
			
			JSONArray jArray = (JSONArray) json__.get(list);

			ObjectMapper objectMapper = new ObjectMapper();
			ArrayList<String> numList = new ArrayList<>();			
			
			for (int i = 0; i < jArray.size(); i++) {
				JsonNode node = objectMapper.readValue(jArray.get(i).toString(), JsonNode.class);
				numList.add("{");
				
				for (int j = 0; j < filter.size(); j++) {
					String typeNested = filter.get(j).toString();
					System.out.println(typeNested);
					String[] nested = typeNested.split("#");
					if(nested.length > 1) {
						JsonNode child = node.get(nested[0]);
						if (child.get(nested[1]) != null) {
							JsonNode childField = child.get(nested[1]);
							String field = childField.asText();
							numList.add("\"" + nested[0] + "_" + nested[1] + "\":" + field);
						} else {
							String field = "null";					
							numList.add("\"" + nested[0] + "\":" + "\"" + "null" + "\"");
						}
					}else {
						JsonNode nameNode = node.get(typeNested);
						String name = nameNode.asText();
						numList.add("\"" + typeNested + "\":" + "\"" + name + "\"");
					}
				}			
				numList.add("}");
			}

//			for (int i = 0; i < jArray.size(); i++) {				
//				JsonNode node = objectMapper.readValue(jArray.get(i).toString(), JsonNode.class);
//				numList.add("{");
//				JsonNode child = node.get("image");
//				if (child.get("product_id") != null) {
//					JsonNode childField = child.get("product_id");
//					String field = childField.asText();
//					numList.add("\"" + "image_" + "product_id" + "\":" + field);
//				} else {
//					String field = "null";					
//					numList.add("\"" + "image" + "\":" + "\"" + "null" + "\"");
//				}
//				JsonNode nameNode = node.get("status");
//				String name = nameNode.asText();
//				numList.add("\"" + "status" + "\":" + "\"" + name + "\"");
//				numList.add("}");
//			}
			String obj = numList.toString();
			obj = obj.replace("{, ", "{").replace(", }", "}");
			Object ob = obj;
			ObjectMapper mapper = new ObjectMapper();

			jsonResultToBucket = mapper.writeValueAsString(ob);
			jsonResultToBucket = jsonResultToBucket.replace("\\", "").replace("\"[", "[").replace("]\"", "]");
			System.out.println("jsonResultToBucket = " + jsonResultToBucket);

			// Upload File Cloud Storage
			Credentials credentials = GoogleCredentials.fromStream(new FileInputStream("c:/bbdd-wild-lama-b1759cfc195e.json"));
			//Credentials credentials = GoogleCredentials.fromStream(new FileInputStream("path/to/file"));
			Storage storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId("bbdd-wild-lama").build().getService();
			
			String contentType = "application/json";
			StorageClass storageClass = StorageClass.STANDARD;
			storage = StorageOptions.getDefaultInstance().getService();
			Bucket bucket = storage.create(BucketInfo.of("sample-wildbrands"));
			String value = jsonResultToBucket.toString().replace("\\", "");
			byte[] bytes = value.getBytes();
			Blob blob = bucket.create("shopify_list_products", bytes, contentType);
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		} catch (Exception e) {
			log.debug("Exception {}", e.toString());
			e.printStackTrace();
		}

		return jsonResultToBucket;

	}

	private static ObjectMapper mapper = new ObjectMapper();

	public static String filterJsonArray(String array, String keyOne, Object valueOne, String keyTwo, Object valueTwo)
			throws IOException {
		Map[] nodes = mapper.readValue(array, HashMap[].class);

		for (Map node : nodes) {
			if (node.containsKey(keyOne) && node.containsKey(keyTwo)) {
				if (node.get(keyOne).equals(valueOne) && node.get(keyTwo).equals(valueTwo)) {
					return mapper.writeValueAsString(node);
				}
			}
		}

		return null;
	}

}
