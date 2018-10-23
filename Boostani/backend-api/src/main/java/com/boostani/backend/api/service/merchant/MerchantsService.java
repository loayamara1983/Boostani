package com.boostani.backend.api.service.merchant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.boostani.backend.api.web.response.campaign.Banners;
import com.boostani.backend.api.web.response.campaign.Campaign;
import com.boostani.backend.api.web.response.campaign.CampaignBanner;
import com.boostani.backend.api.web.response.campaign.Login;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MerchantsService {

	@Autowired
	protected RestTemplate restTemplate;

	@Autowired
	protected Environment env;

	protected ObjectMapper mapper = new ObjectMapper();

	protected HttpHeaders getDefaultHeaders() {
		String origin = env.getProperty("com.boostani.header.origin");
		String referer = env.getProperty("com.boostani.header.referer");

		HttpHeaders headers = new HttpHeaders();

		headers.setAccept(Collections.singletonList(MediaType.ALL));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		headers.add("Origin", origin);
		headers.add("Accept-Encoding", "gzip, deflate");
		headers.add("Accept-Language", "en-US,en;q=0.9,ar;q=0.8");
		headers.add("Referer", referer);

		return headers;
	}
	
	public String getDefaultUrl() {
		return env.getProperty("com.boostani.base.url");
	}

	public String getAdminSessionId() {
		String username = env.getProperty("com.boostani.admin.username");
		String password = env.getProperty("com.boostani.admin.password");

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

		String sessionIdFormData = env.getProperty("com.boostani.request.session.id");
		String formData = String.format(sessionIdFormData, username, password, "M");
		map.add("D", formData);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
				getDefaultHeaders());

		ResponseEntity<Login> response = restTemplate.postForEntity(getDefaultUrl(), request, Login.class);

		List<List<String>> fields = response.getBody().getFields();
		if (fields.size() < 8) {
			return null;
		}

		return fields.get(7).get(1);
	}

	@SuppressWarnings("rawtypes")
	protected List<CampaignBanner> getAllBanners(String sessionId) {
		String url = env.getProperty("com.boostani.base.url");
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

		String listFormRequestData = env.getProperty("com.boostani.request.campain.by.banner.list.form");
		String formData = String.format(listFormRequestData, 0, 100, sessionId);

		map.add("D", formData);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
				getDefaultHeaders());

		ResponseEntity<List> response = restTemplate.postForEntity(url, request, List.class);
		Object bannersObject = response.getBody().get(0);

		Banners bannersList = mapper.convertValue(bannersObject, Banners.class);

		List<List<String>> rows = bannersList.getRows();
		if (rows == null || rows.isEmpty()) {
			return new ArrayList<>();
		}

		rows.remove(0);

		List<CampaignBanner> campaignsBanners = new ArrayList<>();

		for (List<String> row : rows) {
			CampaignBanner campaignBanner = new CampaignBanner();

			campaignBanner.setId(row.get(0));
			campaignBanner.setName(row.get(8));
			campaignBanner.setCampaignId(row.get(3));

			String status = row.get(6) != null && row.get(6).equals("A") ? "Active" : "Inactive";
			campaignBanner.setStatus(status);

			campaignBanner.setType(row.get(5));
			campaignBanner.setUrl(row.get(14));

			campaignsBanners.add(campaignBanner);
		}

		return campaignsBanners;
	}

	protected List<Campaign> populateCampaigns(String sessionId, List<List<String>> rows, List<CampaignBanner> banners) {
		if (rows == null || rows.isEmpty()) {
			return Collections.emptyList();
		}

		List<Campaign> campaigns = new ArrayList<>();

		for (List<String> row : rows) {
			Campaign campaign = new Campaign();

			campaign.setId(row.get(0));
			campaign.setCampaignId(row.get(1));
			String status = row.get(2) != null && row.get(2).equals("A") ? "Active" : "Inactive";
			campaign.setStatus(status);

			campaign.setName(row.get(3));
			campaign.setDescription(row.get(4));

			String logoUrl = row.get(5);
			if (StringUtils.isNotBlank(logoUrl) && !StringUtils.startsWith(logoUrl, "http://")) {
				logoUrl = "http:" + logoUrl;
			}

			campaign.setLogoUrl(logoUrl);
			campaign.setCookieLifetime(row.get(6));

			banners.stream()
					.filter(banner -> banner.getCampaignId().equals(campaign.getId()) && banner.getType().equals("I"))
					.findFirst().ifPresent(imageBanner -> {
						campaign.setBanner(imageBanner);
					});

			String commissions = getCommissionsByCampainId(sessionId, campaign.getCampaignId());
			campaign.setCommissionsDetails(commissions);

			campaigns.add(campaign);
		}

		return campaigns;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected String getCommissionsByCampainId(String sessionId, String campaignId) {
		String url = env.getProperty("com.boostani.base.url");
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

		String listFormRequestData = env.getProperty("com.boostani.request.campain.by.commissions.list.form");
		String formData = String.format(listFormRequestData, campaignId, campaignId, campaignId, sessionId);

		map.add("D", formData);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
				getDefaultHeaders());

		ResponseEntity<List> response = restTemplate.postForEntity(url, request, List.class);
		List<List<String>> commissionsList = (List<List<String>>) response.getBody().get(2);

		if (commissionsList == null || commissionsList.isEmpty()) {
			return "0$";
		}

		commissionsList.remove(0);

		Double totalCommissions = 0.0;

		for (List<String> rows : commissionsList) {
			String type = rows.get(3);
			if (type.equals("$")) {
				String amount = rows.get(4) == null ? "0" : rows.get(4);
				totalCommissions += Double.parseDouble(amount);
			}
		}

		return totalCommissions + "$";
	}

}
