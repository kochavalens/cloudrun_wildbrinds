package com.ms.consume.api.controller.services;

public interface ILoadBigQueryService {
	
	Object loadBigQuery(String[] filter, String nameFile, String nameBucket, String nameDataSet, String nameTable) throws Exception;

}
