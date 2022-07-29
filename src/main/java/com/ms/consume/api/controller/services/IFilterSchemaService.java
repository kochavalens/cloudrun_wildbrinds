package com.ms.consume.api.controller.services;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface IFilterSchemaService {
	
	public Object filterSchema(Object list, JSONArray filter, JSONObject jsonBody);

}
