package com.boostani.backend.api.web.controller.campaign;

import java.util.Collections;
import java.util.List;

import com.boostani.backend.api.web.controller.user.Response;

import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author Loay
 *
 */
public class CampaignListResponse extends Response{

	@ApiModelProperty(notes = "Lists the campains retrieved from Boostani backend server")
	private List<Campaign> campaigns = Collections.emptyList();

	public List<Campaign> getCampaigns() {
		return campaigns;
	}

	public void setCampaigns(List<Campaign> campaigns) {
		this.campaigns = campaigns;
	}
}
