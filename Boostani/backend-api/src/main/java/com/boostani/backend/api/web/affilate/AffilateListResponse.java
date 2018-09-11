package com.boostani.backend.api.web.affilate;

import java.util.Collections;
import java.util.List;

import com.boostani.backend.api.web.Response;
import com.boostani.backend.api.web.campain.Campaign;

public class AffilateListResponse extends Response{

	private List<Campaign> campaigns = Collections.emptyList();

	public List<Campaign> getCampaigns() {
		return campaigns;
	}

	public void setCampaigns(List<Campaign> campaigns) {
		this.campaigns = campaigns;
	}
}
