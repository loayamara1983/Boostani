package com.boostani.backend.api.web.campain;

import com.boostani.backend.api.web.Response;

public class CampainResponse extends Response{

	private Campaign campaign;

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}
}
