package com.ms.consume.api.controller.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.ms.consume.api.controller.ConsumeAPIController;

@Service
public class FilterSchemaServiceImpl implements IFilterSchemaService {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(ConsumeAPIController.class);

	@SuppressWarnings("deprecation")
	public Object filterSchemaBody(String list, String[] filter, JSONObject jsonBody)
			throws IOException {

		JSONArray jArray = (JSONArray) jsonBody.get(list);
		JsonFactory factory = new JsonFactory();
		ObjectMapper mapper = new ObjectMapper(factory);
		// JsonNode rootNode = mapper.readTree(jArray.toString());

		String jsonResultToBucket = "";
		String csv = "";
		String listS = list.toString();
		ArrayList<String> numList = new ArrayList<>();
		JsonNode node = null;
		JsonNode child = null;
		String flag = "";
		for (int i = 0; i < jArray.size(); i++) {
			//node = mapper.readValue(jArray.get(i).toString(), JsonNode.class);
			numList.add("{");
			for (int j = 0; j < filter.length; j++) {
				node = mapper.readValue(jArray.get(i).toString(), JsonNode.class);
				String typeNested = filter[j]; //filter.get(j).toString();
				System.out.println(typeNested);
				String[] nested = typeNested.split("#");
				if (nested.length > 1) {
					for (int k = 0; k < node.size(); k++) {
						flag = "";
						for (int l = 0; l < nested.length; l++) {
							if (node.getNodeType() == JsonNodeType.ARRAY) {
								if(node.get(0) == null) {
									System.out.println(typeNested.replace("#", "_") + " : " + "null");
									numList.add("\"" + typeNested.replace("#", "_") + "\":" + "\"" + "null" + "\"");
									l = nested.length + 1;
									k = node.size() + 1;
								}else {
									if (node.get(0).get(nested[l]) == null) {
										System.out.println(typeNested.replace("#", "_") + " : " + "null");
										numList.add("\"" + typeNested.replace("#", "_") + "\":" + "\"" + "null" + "\"");
										l = nested.length + 1;
										k = node.size() + 1;
									} else {
										child = node.get(0).get(nested[l]);
										if (l == nested.length - 1) {
											System.out.println(typeNested.replace("#", "_") + " : " + child);
											numList.add("\"" + typeNested.replace("#", "_") + "\":" + "\"" + child + "\"");
											l++;
											k = node.size() + 1;
											flag = "0";
										}
									}
								}
							}else {
								if (node.get(nested[l]) == null) {
									System.out.println(typeNested.replace("#", "_") + " : " + "null");
									numList.add("\"" + typeNested.replace("#", "_") + "\":" + "\"" + "null" + "\"");
									l = nested.length + 1;
									k = node.size() + 1;
								} else {
									child = node.get(nested[l]);
									if (l == nested.length - 1) {
										System.out.println(typeNested.replace("#", "_") + " : " + child);
										numList.add("\"" + typeNested.replace("#", "_") + "\":" + "\"" + child + "\"");
										l++;
										k = node.size() + 1;
										flag = "0";
									}
								}
								if(!flag.equals("0")) {
									node = child;
								}
							}													
						}
					}
				} else {
					node = mapper.readValue(jArray.get(i).toString(), JsonNode.class);					
					child = node.get(typeNested);
					System.out.println(typeNested + " : " + child);		
					numList.add("\"" + typeNested + "\":" + "\"" + child + "\"");
				}

			}
			numList.add("}");									
			// traverse(rootNode, 1);
		}
		String obj = numList.toString();
		obj = obj.replace("{, ", "{").replace(", }", "}");
		Object ob = obj;
		ObjectMapper mapper__ = new ObjectMapper();

		jsonResultToBucket = mapper__.writeValueAsString(ob);
		jsonResultToBucket = "{\"" + listS + "\":"
				+ jsonResultToBucket.replace("\"[{", "[{").replace("]\"", "]").replace("\\", "").replace("\"\"", "\"") + "}";
		System.out.println("jsonResultToBucket = " + jsonResultToBucket);

		org.json.JSONObject output;
		output = new org.json.JSONObject(jsonResultToBucket);
		org.json.JSONArray docs = output.getJSONArray(listS);
		File file = new File("listProducts.csv");
		csv = CDL.toString(docs);
		FileUtils.writeStringToFile(file, csv);
		System.out.println("Data has been Sucessfully Writeen to " + file);
		System.out.println(csv);
		return csv;
	}

	private void traverse(JsonNode node, int level) {

		if (node.getNodeType() == JsonNodeType.ARRAY) {
			traverseArray(node, 1);
		} else if (node.getNodeType() == JsonNodeType.OBJECT) {
			traverseObject(node, 1);
		} else {
			throw new RuntimeException("Not yet implemented");
		}
	}

	private void traverseObject(JsonNode node, int level) {
		node.fieldNames().forEachRemaining((String fieldName) -> {
			JsonNode childNode = node.get(fieldName);
			printNode(childNode, fieldName, level);
			// for nested object or arrays
			if (traversable(childNode)) {
				traverse(childNode, level + 1);
			}
		});
	}

	private void traverseArray(JsonNode node, int level) {
		for (JsonNode jsonArrayNode : node) {
			printNode(jsonArrayNode, "arrayElement", level);
			if (traversable(jsonArrayNode)) {
				traverse(jsonArrayNode, level + 1);
			}
		}
	}

	private static boolean traversable(JsonNode node) {
		return node.getNodeType() == JsonNodeType.OBJECT || node.getNodeType() == JsonNodeType.ARRAY;
	}

	private static void printNode(JsonNode node, String keyName, int level) {
		if (traversable(node)) {
			System.out.printf("%" + (level * 4 - 3) + "s|-- %s=%s type=%s%n", "", keyName, node.toString(),
					node.getNodeType());
		} else {
			Object value = null;
			if (node.isTextual()) {
				value = node.textValue();
			} else if (node.isNumber()) {
				value = node.numberValue();
			} // todo add more types
			System.out.printf("%" + (level * 4 - 3) + "s|-- %s=%s type=%s%n", "", keyName, value, node.getNodeType());
		}
	}

}
