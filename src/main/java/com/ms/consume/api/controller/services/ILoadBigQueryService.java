package com.ms.consume.api.controller.services;

import org.json.simple.JSONArray;

public interface ILoadBigQueryService {
	
	Object loadBigQuery(JSONArray filter, String nameFile, String nameBucket, String nameDataSet, String nameTable) throws Exception;

}
