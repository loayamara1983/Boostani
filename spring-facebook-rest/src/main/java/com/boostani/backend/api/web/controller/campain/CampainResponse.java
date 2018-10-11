package com.boostani.backend.api.web.controller.campain;

import com.boostani.backend.api.web.controller.user.Response;

import io.swagger.annotations.ApiModelProperty;

public class CampainResponse extends Response{

	@ApiModelProperty(notes = "Displays the campain retrieved from Boostani backend server")
	private Campaign campaign;

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}
}
