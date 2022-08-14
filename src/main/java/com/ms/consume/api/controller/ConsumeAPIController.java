package com.ms.consume.api.controller;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ms.consume.api.controller.services.FilterSchemaServiceImpl;
import com.ms.consume.api.controller.services.LoadBigQueryServiceImpl;
import com.ms.consume.api.controller.services.UploadBucketServiceImpl;
import com.ms.consume.api.models.RequestAPIConsume;
import com.ms.consume.api.models.RequestSchema;

@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS,
		RequestMethod.HEAD })
@RestController
@RequestMapping(value = { "/api" })
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

	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	@PostMapping("/consumeApis")
	public String consumeAPIS(@RequestBody RequestAPIConsume request) {

		Object response = null;
		JSONObject jsonBody = null;
		Object responseFilter = "";
		Object uploadBucket = "";
		Object loadBigQuery = "";

		String client_id = "";
		String client_password = "";
		String token = "";
		String username = "";
		String password = "";

		try {
			RequestAPIConsume rqBody = request;
			JSONParser parser = new JSONParser();
			// Object jsonrow = parser.parse(rqBody);
			// JSONObject jsonrow = (JSONObject) parser.parse(String.valueOf(rqBody));

			// CREDENTIAL AUTHORIZATION
			client_id = rqBody.getClient_id();
			client_password = rqBody.getClient_password();
			token = rqBody.getAccess_token();

			username = rqBody.getUsername();
			password = rqBody.getPassword();

			HttpHeaders headers = new HttpHeaders();
			headers.set("X-Shopify-Access-Token", token);
			headers.setBasicAuth(username, password);
			HttpEntity requestEntity = new HttpEntity(headers);

			Map<String, String> vars = new HashMap<>();
			vars.put("client_id", client_id);
			vars.put("client_password", client_password);
			////////////////////////////////////////////////////////////////////////////////////////////////

			// ENDPOINT
			String url = rqBody.getUrl();
			////////////////////////////////////////////////////////////////////////////////////////////////

			// SCHEMA
			String enableBucket = rqBody.getEnableBucket();
			String enableBigQuery = rqBody.getEnableBigQuery();
			String nameFile = rqBody.getNameFile();
			String nameBucket = rqBody.getNameBucket();
			String nameDataset = rqBody.getNameDataset();
			String nameTable = rqBody.getNameTable();
			RequestSchema schema = rqBody.getSchema();
			String list = schema.getList();
			String[] filter = schema.getFilter();
			////////////////////////////////////////////////////////////////////////////////////////////////

			System.out.println("url: " + url);
			System.out.println("schema: " + schema);
			System.out.println("list: " + list);
			System.out.println("filter: " + filter);

			// Consume API External
			response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Object.class, vars);

			System.out.println("response: " + response);

			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String responseJson = ow.writeValueAsString(response);
			// System.out.println("responseJson: " + responseJson);

			JSONParser parser_ = new JSONParser();
			JSONObject jsonO = (JSONObject) parser_.parse(responseJson);

			jsonBody = new JSONObject();
			jsonBody = (JSONObject) jsonO.get("body");
			System.out.println("jsonBody: " + jsonBody);
			////////////////////////////////////////////////////////////////////////////////////////////////

			responseFilter = filterSchemaServiceImpl.filterSchemaBody(list, filter, jsonBody);

			// Upload Bucket
			if (enableBucket.equals("ON")) {
				uploadBucket = uploadBucketServiceImpl.uploadBucket(nameFile, nameBucket, responseFilter.toString());
			}

			// load BigQuery
			if (enableBigQuery.equals("ON")) {
				loadBigQuery = loadBigQueryServiceImpl.loadBigQuery(filter, nameFile, nameBucket, nameDataset,
						nameTable);
			}

		} catch (Exception e) {
			log.debug("Exception {}", e.toString());
			e.printStackTrace();
		}

		return responseFilter.toString();

	}

}
