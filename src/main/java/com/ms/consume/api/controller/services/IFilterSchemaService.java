package com.ms.consume.api.controller.services;

import java.io.IOException;

import org.json.simple.JSONObject;

public interface IFilterSchemaService {
	
	public Object filterSchemaBody(String list, String[] filter, JSONObject jsonBody) throws IOException;

}
