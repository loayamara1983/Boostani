package com.boostani.backend.api.service.affiliate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import com.boostani.backend.api.persistance.model.User;
import com.boostani.backend.api.service.merchant.MerchantsService;
import com.boostani.backend.api.service.user.UserNotFoundException;
import com.boostani.backend.api.web.response.campaign.Campaign;
import com.boostani.backend.api.web.response.campaign.CampaignBanner;
import com.boostani.backend.api.web.response.campaign.Campaigns;

@Service
@CacheConfig(cacheNames = "affliates")
public class AffliateService extends MerchantsService {

	@Cacheable
	public List<Campaign> findCampains(User currentUser, int pageNumber, int pageSize) throws UserNotFoundException {

		String sessionId = getAdminSessionId();
		if (StringUtils.isBlank(sessionId)) {
			throw new UserNotFoundException("Unauthorized access to Boostani Backend");
		}

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

		String campaignsListFormRequestData = env.getProperty("com.boostani.request.affiliate.campain.list.form");
		String formData = String.format(campaignsListFormRequestData, pageNumber, pageSize, sessionId);
		map.add("D", formData);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, getDefaultHeaders());

		ResponseEntity<List> campaignsResponse = restTemplate.postForEntity(getDefaultUrl(), request, List.class);
		Object campaignsObject = campaignsResponse.getBody().get(0);

		Campaigns campaignsList = mapper.convertValue(campaignsObject, Campaigns.class);

		List<List<String>> rows = campaignsList.getRows();
		if (rows == null || rows.isEmpty()) {
			return Collections.emptyList();
		}

		rows.remove(0);

		List<CampaignBanner> banners = getAllBanners(sessionId);

		List<Campaign> campaigns = populate(sessionId, rows, banners);
		return getCampaignsForAffliate(sessionId, campaigns, currentUser);
	}

	protected List<Campaign> populate(String sessionId, List<List<String>> rows, List<CampaignBanner> banners) {
		if (rows == null || rows.isEmpty()) {
			return Collections.emptyList();
		}

		List<Campaign> campaigns = new ArrayList<>();

		for (List<String> row : rows) {
			Campaign campaign = new Campaign();

			campaign.setId(row.get(0));
			campaign.setCampaignId(row.get(1));

			String status = row.get(3) != null && row.get(3).equals("A") ? "Active" : "Inactive";
			campaign.setStatus(status);

			campaign.setName(row.get(4));
			campaign.setDescription(row.get(5));

			String logoUrl = row.get(10);
			if (StringUtils.isNotBlank(logoUrl) && !StringUtils.startsWith(logoUrl, "http://")) {
				logoUrl = "http:" + logoUrl;
			}

			campaign.setLogoUrl(logoUrl);

			campaign.setCookieLifetime(row.get(18));
			List<CampaignBanner> campainBanners = banners.stream()
					.filter(banner -> banner.getCampaignId().equals(campaign.getId())).collect(Collectors.toList());
			campaign.setBanners(campainBanners);

			campaign.setCommissionsDetails(getCommissionsByCampainId(sessionId, campaign.getCampaignId()));

			campaigns.add(campaign);
		}

		return campaigns;
	}

	@SuppressWarnings("unchecked")
	private List<Campaign> getCampaignsForAffliate(String sessionId, List<Campaign> campaigns, User currentUser) {
		if (campaigns == null || campaigns.isEmpty()) {
			return Collections.emptyList();
		}

		List<Campaign> affliateCampaigns = new ArrayList<>();

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, getDefaultHeaders());
		String campaignsListFormRequestData = env.getProperty("com.boostani.request.campain.affiliates.list.form");

		for (Campaign campaign : campaigns) {
			String campaignId = campaign.getCampaignId();

			String formData = String.format(campaignsListFormRequestData, campaignId, sessionId);
			map.add("D", formData);

			ResponseEntity<List> campaignAffliatesResponse = restTemplate.postForEntity(getDefaultUrl(), request,
					List.class);
			List<List<String>> rows = campaignAffliatesResponse.getBody();
			if (rows == null || rows.isEmpty()) {
				continue;
			}

			Map affliatesRows = (Map) rows.get(2);
			List<List<String>> affliatesRow = (List) affliatesRows.get("rows");
			affliatesRow.remove(0);

			for (List<String> row : affliatesRow) {
				String username = row.get(4);
				if (username.equals(currentUser.getUsername())) {
					affliateCampaigns.add(campaign);
				}
			}
		}

		return affliateCampaigns;
	}

	@SuppressWarnings("rawtypes")
	public String getBalanceForAffliate(User currentUser) throws UserNotFoundException {
		try {
			String sessionId = getAffiliateSessionId(currentUser.getUsername(), currentUser.getPassword());
			if (StringUtils.isBlank(sessionId)) {
				throw new UserNotFoundException("Unauthorized access to Boostani Backend");
			}

			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

			String statsRequestData = env.getProperty("com.boostani.request.campain.affiliate.stats");
			String formData = String.format(statsRequestData, sessionId);
			map.add("D", formData);

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, getDefaultHeaders());

			ResponseEntity<Object> statsResponse = restTemplate.postForEntity(getDefaultUrl(), request, Object.class);
			List statsList = (List)statsResponse.getBody();
			List fields = (List)statsList.get(6);
			List totalCommisions = (List)fields.get(2);
			
			return totalCommisions.get(1).toString();
			
		} catch (RestClientException e) {
			e.printStackTrace();
			return "0";
		}
	}
}
