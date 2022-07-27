package com.ms.consume.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "URL", "CLIENT_ID", "CLIENT_PASSWORD", "ACCESS_TOKEN" })
public class RequestShopify {

	@JsonProperty("URL")
	private String url;
	@JsonProperty("CLIENT_ID")
	private String client_id;
	@JsonProperty("CLIENT_PASSWORD")
	private String client_password;
	@JsonProperty("ACCESS_TOKEN")
	private String access_token;

	public RequestShopify() {

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

	@Override
	public String toString() {
		return "RequestShopify [url=" + url + ", client_id=" + client_id + ", client_password=" + client_password
				+ ", access_token=" + access_token + "]";
	}

}
