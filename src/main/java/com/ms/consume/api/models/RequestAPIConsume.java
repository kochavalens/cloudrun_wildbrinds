package com.ms.consume.api.models;

public class RequestAPIConsume {

	private String url;
	private String client_id;
	private String client_password;
	private String access_token;
	private String username;
	private String password;
	private String enableBucket;
	private String nameFile;
	private String nameBucket;
	private String enableBigQuery;
	private String nameDataset;
	private String nameTable;
	private RequestSchema schema;

	public RequestAPIConsume() {

	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getClient_password() {
		return client_password;
	}

	public void setClient_password(String client_password) {
		this.client_password = client_password;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
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

	public String getEnableBucket() {
		return enableBucket;
	}

	public void setEnableBucket(String enableBucket) {
		this.enableBucket = enableBucket;
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

	public String getEnableBigQuery() {
		return enableBigQuery;
	}

	public void setEnableBigQuery(String enableBigQuery) {
		this.enableBigQuery = enableBigQuery;
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
