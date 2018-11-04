package com.boostani.backend.api.web.response.campaign;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class CampaignBanner {

	@JsonIgnore
	private String id;
	
	private String name;
	
	@JsonIgnore
	private String campaignId;
	
	private String url;
	
//	@JsonIgnore
	private String type;
	
	private String status;
	
	private String destinationUrl;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDestinationUrl() {
		return destinationUrl;
	}

	public void setDestinationUrl(String destinationUrl) {
		this.destinationUrl = destinationUrl;
	}

	@Override
	public String toString() {
		return "CampaignBanner [id=" + id + ", name=" + name + ", campaignId=" + campaignId + ", url=" + url + ", type="
				+ type + ", status=" + status + "]";
	}
	
	
}
