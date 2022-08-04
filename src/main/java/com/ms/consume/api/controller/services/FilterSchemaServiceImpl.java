package com.ms.consume.api.controller.services;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.consume.api.controller.ConsumeAPIController;

@Service
public class FilterSchemaServiceImpl implements IFilterSchemaService {

	private static Logger log = LoggerFactory.getLogger(ConsumeAPIController.class);

	public Object filterSchema(Object list, JSONArray filter, JSONObject jsonBody) {

		String jsonResultToBucket = "";
		String csv = "";
		String listS = list.toString();
		
		JSONArray jArray = (JSONArray) jsonBody.get(list);

		ObjectMapper objectMapper = new ObjectMapper();
		ArrayList<String> numList = new ArrayList<>();

		try {
			for (int i = 0; i < jArray.size(); i++) {

				JsonNode node = objectMapper.readValue(jArray.get(i).toString(), JsonNode.class);
				numList.add("{");

				for (int j = 0; j < filter.size(); j++) {
					String typeNested = filter.get(j).toString();
					System.out.println(typeNested);
					String[] nested = typeNested.split("#");
					if (nested.length == 5) {
						JsonNode child = node.get(nested[0]);
						if (child.get(nested[1]) != null) {
							JsonNode childField = child.get(nested[1]);
							if (childField.get(nested[2]) != null) {
								JsonNode childField1 = childField.get(nested[2]);
								if (childField1.get(nested[3]) != null) {
									JsonNode childField2 = childField1.get(nested[3]);
									if (childField2.get(nested[4]) != null) {
										JsonNode childField4 = childField2.get(nested[4]);
										String field = childField4.asText();
										numList.add("\"" + nested[0] + "_" + nested[1] + "_" + nested[2] + "_" + nested[3] + "_" + nested[4] + "\":" + field);
									} else {
										String field = "null";
										numList.add("\"" + nested[0] + "_" + nested[1] + "_" + nested[2] + "_" + nested[3] + "_" + nested[4] + "\":" + field);										
									}
								} else {
									String field = "null";
									numList.add("\"" + nested[0] + "\":" + "\"" + "null" + "\"");
								}														
							} else {
								String field = "null";
								numList.add("\"" + nested[0] + "\":" + "\"" + "null" + "\"");
							}																				
						} else {
							String field = "null";
							numList.add("\"" + nested[0] + "\":" + "\"" + "null" + "\"");
						}										
						
					} else if (nested.length > 1) {
						JsonNode child = node.get(nested[0]);
						if (child.get(nested[1]) != null) {
							JsonNode childField = child.get(nested[1]);
							String field = childField.asText();
							numList.add("\"" + nested[0] + "_" + nested[1] + "\":" + field);
						} else {
							String field = "null";
							numList.add("\"" + nested[0] + "\":" + "\"" + "null" + "\"");
						}
					} else {
						JsonNode nameNode = node.get(typeNested);
						String name = nameNode.asText();
						numList.add("\"" + typeNested + "\":" + "\"" + name + "\"");
					}
				}
				numList.add("}");
			}

			String obj = numList.toString();
			obj = obj.replace("{, ", "{").replace(", }", "}");
			Object ob = obj;
			ObjectMapper mapper = new ObjectMapper();

			jsonResultToBucket = mapper.writeValueAsString(ob);
			jsonResultToBucket = "{\"" + listS + "\":" + jsonResultToBucket.replace("\"[{", "[{").replace("]\"", "]").replace("\\", "") + "}";
			System.out.println("jsonResultToBucket = " + jsonResultToBucket);

			org.json.JSONObject output;
			output = new org.json.JSONObject(jsonResultToBucket);
			org.json.JSONArray docs = output.getJSONArray(listS);
			File file = new File("listProducts.csv");
			csv = CDL.toString(docs);
			FileUtils.writeStringToFile(file, csv);
			System.out.println("Data has been Sucessfully Writeen to " + file);
			System.out.println(csv);

		} catch (Exception e) {
			log.debug("Exception {}", e.toString());
			e.printStackTrace();
		}

		return csv;

	}

}
