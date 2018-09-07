package com.boostani.backend.api.web.campain;

import java.util.List;

/**
 * 
 * @author Loay
 *
 */
public class CampainListResponse {

	private List<Campaign> campaigns;

	public List<Campaign> getCampaigns() {
		return campaigns;
	}

	public void setCampaigns(List<Campaign> campaigns) {
		this.campaigns = campaigns;
	}
}
