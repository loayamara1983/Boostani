package com.boostani.backend.api.web.campain;

import java.util.Collections;
import java.util.List;

import com.boostani.backend.api.web.Response;

/**
 * 
 * @author Loay
 *
 */
public class CampainListResponse extends Response{

	private List<Campaign> campaigns = Collections.emptyList();

	public List<Campaign> getCampaigns() {
		return campaigns;
	}

	public void setCampaigns(List<Campaign> campaigns) {
		this.campaigns = campaigns;
	}
}
