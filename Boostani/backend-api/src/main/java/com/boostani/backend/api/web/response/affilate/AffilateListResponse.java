package com.boostani.backend.api.web.response.affilate;

import java.util.Collections;
import java.util.List;

import com.boostani.backend.api.web.response.Response;
import com.boostani.backend.api.web.response.campaign.Campaign;

import io.swagger.annotations.ApiModelProperty;

public class AffilateListResponse extends Response{

	@ApiModelProperty(notes = "Lists all campains for this particualler affliate from Boostani backend server")
	private List<Campaign> campaigns = Collections.emptyList();

	public List<Campaign> getCampaigns() {
		return campaigns;
	}

	public void setCampaigns(List<Campaign> campaigns) {
		this.campaigns = campaigns;
	}
}
