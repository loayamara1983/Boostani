package com.boostani.backend.api.web.affilate;

import java.util.List;

import com.boostani.backend.api.web.campain.Campaign;

public class AffilateListResponse {

	private List<Campaign> campaigns;

	public List<Campaign> getCampaigns() {
		return campaigns;
	}

	public void setCampaigns(List<Campaign> campaigns) {
		this.campaigns = campaigns;
	}
}
