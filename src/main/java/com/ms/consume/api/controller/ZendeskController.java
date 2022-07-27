package com.ms.consume.api.controller;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;
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

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;

@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS,
		RequestMethod.HEAD })
@RestController
@RequestMapping({ "/api/zendesk" })
public class ZendeskController {
	
	private static Logger log = LoggerFactory.getLogger(ZendeskController.class);
	
private RestTemplate restTemplate = new RestTemplate();
	
	@Autowired
	public ZendeskController(RestTemplate resTemplare) {
		this.restTemplate = restTemplate;
	}
	
	@GetMapping("/listTickets")
	public Object listTickets(@RequestBody String request){
		
		Object response = null;
		
		try {
		Gson zendesk = new Gson();
		Properties properties = zendesk.fromJson(request, Properties.class);
		String username = properties.getProperty("username");
		String password = properties.getProperty("password");
		String url = properties.getProperty("url");
		
		HttpHeaders headers = new HttpHeaders();
		
		headers.setBasicAuth(username, password);
		
		HttpEntity requestEntity = new HttpEntity(headers);
		
		response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Object.class);
		
		} catch (Exception e) {			
			 log.debug("Exception {}", e.toString());
	        e.printStackTrace();
		}

		return response;
		
	}

}
