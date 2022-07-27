package com.ms.consume.api.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class ShopifyProduct {

	private String id;
	private String title;
	private String body_html;
	private String vendor;
	private String product_type;
	private String created_at;
	private String handle;
	private String updated_at;
	private String published_at;
	private String template_suffix;
	private String status;
	private String published_scope;
	private String tags;
	private String admin_graphql_api_id;
	private List<String> variants;
	private List<String> options;
	private List<String> images;
	private List<String> image;

	public ShopifyProduct() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody_html() {
		return body_html;
	}

	public void setBody_html(String body_html) {
		this.body_html = body_html;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getProduct_type() {
		return product_type;
	}

	public void setProduct_type(String product_type) {
		this.product_type = product_type;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public String getPublished_at() {
		return published_at;
	}

	public void setPublished_at(String published_at) {
		this.published_at = published_at;
	}

	public String getTemplate_suffix() {
		return template_suffix;
	}

	public void setTemplate_suffix(String template_suffix) {
		this.template_suffix = template_suffix;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPublished_scope() {
		return published_scope;
	}

	public void setPublished_scope(String published_scope) {
		this.published_scope = published_scope;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getAdmin_graphql_api_id() {
		return admin_graphql_api_id;
	}

	public void setAdmin_graphql_api_id(String admin_graphql_api_id) {
		this.admin_graphql_api_id = admin_graphql_api_id;
	}

	public List<String> getVariants() {
		return variants;
	}

	public void setVariants(List<String> variants) {
		this.variants = variants;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	public List<String> getImage() {
		return image;
	}

	public void setImage(List<String> image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "ShopifyProduct [id=" + id + ", title=" + title + ", body_html=" + body_html + ", vendor=" + vendor
				+ ", product_type=" + product_type + ", created_at=" + created_at + ", handle=" + handle
				+ ", updated_at=" + updated_at + ", published_at=" + published_at + ", template_suffix="
				+ template_suffix + ", status=" + status + ", published_scope=" + published_scope + ", tags=" + tags
				+ ", admin_graphql_api_id=" + admin_graphql_api_id + ", variants=" + variants + ", options=" + options
				+ ", images=" + images + ", image=" + image + "]";
	}

}
