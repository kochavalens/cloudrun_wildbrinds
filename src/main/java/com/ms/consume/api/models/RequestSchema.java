package com.ms.consume.api.models;

public class RequestSchema {

	private String list;
	private String[] filter;

	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}

	public String[] getFilter() {
		return filter;
	}

	public void setFilter(String[] filter) {
		this.filter = filter;
	}

}
