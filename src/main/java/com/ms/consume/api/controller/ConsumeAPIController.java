package com.ms.consume.api.controller;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ms.consume.api.controller.services.FilterSchemaServiceImpl;
import com.ms.consume.api.controller.services.UploadBucketServiceImpl;

@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS,
		RequestMethod.HEAD })
@RestController
@RequestMapping({ "/api" })
public class ConsumeAPIController {
	
	private static Logger log = LoggerFactory.getLogger(ConsumeAPIController.class);
	private RestTemplate restTemplate = new RestTemplate();
	
	@Autowired
	FilterSchemaServiceImpl filterSchemaServiceImpl;
	
	@Autowired
	UploadBucketServiceImpl uploadBucketServiceImpl;

	@Autowired
	public ConsumeAPIController(RestTemplate resTemplare) {
		this.restTemplate = restTemplate;
	}

	@GetMapping("/consume")
	public Object consumeAPI(@RequestBody String request) {
		
		Object response = null;
		JSONObject jsonBody = null;
		String jsonResultToBucket = "";
		String csv = "";
		Object responseFilter = "";
		Object uploadBucket = "";
		
		String client_id = "";
		String client_password = "";
		String token = "";
		String username = "";
		String password = "";
		
		try {			
			
			JSONParser parser = new JSONParser();
			JSONObject jsonrow = (JSONObject) parser.parse(String.valueOf(request));

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
			String nameFile = (String) jsonrow.get("nameFile");
			String nameBucket = (String) jsonrow.get("nameBucket");
			JSONObject schema = (JSONObject) jsonrow.get("schema");		
			Object list = schema.get("list");
			JSONArray filter = (JSONArray) schema.get("filter"); 
			////////////////////////////////////////////////////////////////////////////////////////////////
			
			response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Object.class, vars);
			
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String responseJson = ow.writeValueAsString(response);
			System.out.println("responseJson: " + responseJson);
			
			JSONParser parser_ = new JSONParser();
			JSONObject jsonO = (JSONObject) parser_.parse(responseJson);

			jsonBody = new JSONObject();
			jsonBody = (JSONObject) jsonO.get("body");
			
			responseFilter = filterSchemaServiceImpl.filterSchema(list, filter, jsonBody);
			
			uploadBucket = uploadBucketServiceImpl.uploadBucket(nameFile, nameBucket, responseFilter.toString());
			
		} catch (Exception e) {
			log.debug("Exception {}", e.toString());
			e.printStackTrace();
		}
		
		return responseFilter;
		
	}
}
