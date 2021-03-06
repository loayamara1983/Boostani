package com.boostani.backend.api.web.response.campaign;

import com.boostani.backend.api.web.response.Response;

import io.swagger.annotations.ApiModelProperty;

public class CampaignResponse extends Response{

	@ApiModelProperty(notes = "Displays the campain retrieved from Boostani backend server")
	private Campaign campaign;

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}
}
