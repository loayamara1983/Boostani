package com.boostani.backend.api.web.response.campaign;

public class CampaignBanner {

	private String id;
	
	private String name;
	
	private String campaignId;
	
	private String url;
	
	private String type;
	
	private String status;

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

	@Override
	public String toString() {
		return "CampaignBanner [id=" + id + ", name=" + name + ", campaignId=" + campaignId + ", url=" + url + ", type="
				+ type + ", status=" + status + "]";
	}
	
	
}
