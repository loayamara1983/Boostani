package com.boostani.backend.api.web.response.campaign;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * @author Loay
 *
 */

@JsonInclude(value=Include.NON_EMPTY)
public class Campaign implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8580571905763457816L;

	private String id;
	private String campaignId;
	private String status;
	private String name;
	private String description;
	private String logoUrl;
	private String cookieLifetime;
	private String longDescriptionExists;
	private List<CampaignBanner> banners;
	private String commissionsExist;
	private String commissionsDetails;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getCookieLifetime() {
		return cookieLifetime;
	}

	public void setCookieLifetime(String cookieLifetime) {
		this.cookieLifetime = cookieLifetime;
	}

	public String getLongDescriptionExists() {
		return longDescriptionExists;
	}

	public void setLongDescriptionExists(String longDescriptionExists) {
		this.longDescriptionExists = longDescriptionExists;
	}

	public List<CampaignBanner> getBanners() {
		return banners;
	}

	public void setBanners(List<CampaignBanner> banners) {
		this.banners = banners;
	}

	public String getCommissionsExist() {
		return commissionsExist;
	}

	public void setCommissionsExist(String commissionsExist) {
		this.commissionsExist = commissionsExist;
	}

	public String getCommissionsDetails() {
		return commissionsDetails;
	}

	public void setCommissionsDetails(String commissionsDetails) {
		this.commissionsDetails = commissionsDetails;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((campaignId == null) ? 0 : campaignId.hashCode());
		result = prime * result + ((commissionsDetails == null) ? 0 : commissionsDetails.hashCode());
		result = prime * result + ((commissionsExist == null) ? 0 : commissionsExist.hashCode());
		result = prime * result + ((cookieLifetime == null) ? 0 : cookieLifetime.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((logoUrl == null) ? 0 : logoUrl.hashCode());
		result = prime * result + ((longDescriptionExists == null) ? 0 : longDescriptionExists.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Campaign other = (Campaign) obj;
		if (campaignId == null) {
			if (other.campaignId != null)
				return false;
		} else if (!campaignId.equals(other.campaignId))
			return false;
		if (commissionsDetails == null) {
			if (other.commissionsDetails != null)
				return false;
		} else if (!commissionsDetails.equals(other.commissionsDetails))
			return false;
		if (commissionsExist == null) {
			if (other.commissionsExist != null)
				return false;
		} else if (!commissionsExist.equals(other.commissionsExist))
			return false;
		if (cookieLifetime == null) {
			if (other.cookieLifetime != null)
				return false;
		} else if (!cookieLifetime.equals(other.cookieLifetime))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (logoUrl == null) {
			if (other.logoUrl != null)
				return false;
		} else if (!logoUrl.equals(other.logoUrl))
			return false;
		if (longDescriptionExists == null) {
			if (other.longDescriptionExists != null)
				return false;
		} else if (!longDescriptionExists.equals(other.longDescriptionExists))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Campaign [id=" + id + ", campaignId=" + campaignId + ", status=" + status + ", name=" + name
				+ ", description=" + description + ", logoUrl=" + logoUrl + ", cookieLifetime=" + cookieLifetime
				+ ", longDescriptionExists=" + longDescriptionExists + ", commissionsExist="
				+ commissionsExist + ", commissionsDetails=" + commissionsDetails + "]";
	}

}
