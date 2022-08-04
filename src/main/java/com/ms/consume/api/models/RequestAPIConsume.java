package com.ms.consume.api.models;

import java.util.List;

public class RequestAPIConsume {

	private String url;
	private String username;
	private String password;
	private String nameFile;
	private String nameBucket;
	private String nameDataset;
	private String nameTable;
	private RequestSchema schema;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNameFile() {
		return nameFile;
	}

	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}

	public String getNameBucket() {
		return nameBucket;
	}

	public void setNameBucket(String nameBucket) {
		this.nameBucket = nameBucket;
	}

	public String getNameDataset() {
		return nameDataset;
	}

	public void setNameDataset(String nameDataset) {
		this.nameDataset = nameDataset;
	}

	public String getNameTable() {
		return nameTable;
	}

	public void setNameTable(String nameTable) {
		this.nameTable = nameTable;
	}

	public RequestSchema getSchema() {
		return schema;
	}

	public void setSchema(RequestSchema schema) {
		this.schema = schema;
	}

	

}
