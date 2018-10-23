package com.boostani.backend.api.service.campaign;

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

import com.boostani.backend.api.service.merchant.MerchantsService;
import com.boostani.backend.api.service.user.UserNotFoundException;
import com.boostani.backend.api.web.response.campaign.Campaign;
import com.boostani.backend.api.web.response.campaign.CampaignBanner;
import com.boostani.backend.api.web.response.campaign.Campaigns;

@Service
@CacheConfig(cacheNames = "campaigns")
public class CampaignService extends MerchantsService {

	@Cacheable
	public List<Campaign> findAll(int page, int pageSize) throws UserNotFoundException {

		String sessionId = getAdminSessionId();
		if (StringUtils.isBlank(sessionId)) {
			throw new UserNotFoundException("Unauthorized access to Boostani Backend");
		}

		String url = env.getProperty("com.boostani.base.url");

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

		String listFormRequestData = env.getProperty("com.boostani.request.campain.list.form");
		String formData = String.format(listFormRequestData, page, pageSize, sessionId);

		map.add("D", formData);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
				getDefaultHeaders());

		ResponseEntity<List> campaignsResponse = restTemplate.postForEntity(url, request, List.class);
		Object campaignsObject = campaignsResponse.getBody().get(0);

		Campaigns campaignsList = mapper.convertValue(campaignsObject, Campaigns.class);

		List<List<String>> rows = campaignsList.getRows();
		if (rows == null || rows.isEmpty()) {
			return Collections.emptyList();
		}

		rows.remove(0);

		List<CampaignBanner> banners = getAllBanners(sessionId);

		return populateCampaigns(sessionId, rows, banners);
	}

	@Cacheable
	public Campaign findOne(String id) throws UserNotFoundException {
		String url = env.getProperty("com.boostani.base.url");

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

		String idFormRequestData = env.getProperty("com.boostani.request.campain.id.form");
		String sessionId = getAdminSessionId();
		if (StringUtils.isBlank(sessionId)) {
			throw new UserNotFoundException("Unauthorized access to Boostani Backend");
		}

		String formData = String.format(idFormRequestData, id, id, sessionId);

		map.add("D", formData);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
				getDefaultHeaders());

		ResponseEntity<List> campaignsResponse = restTemplate.postForEntity(url, request, List.class);
		Map campaignsObject = (Map) campaignsResponse.getBody().get(0);
		List<List> fields = (List<List>) campaignsObject.get("fields");

		List<CampaignBanner> banners = getAllBanners(sessionId);

		fields.remove(0);

		Campaign campaign = new Campaign();

		campaign.setId(fields.get(0).get(1).toString());
		campaign.setCampaignId(fields.get(1).get(1).toString());
		campaign.setStatus(fields.get(3).get(1).toString());
		campaign.setName(fields.get(4).get(1).toString());
		campaign.setDescription(fields.get(5).get(1).toString());

		String logoUrl = fields.get(11).get(1).toString();
		if (StringUtils.isNotBlank(logoUrl) && !StringUtils.startsWith(logoUrl, "http://")) {
			logoUrl = "http:" + logoUrl;
		}

		campaign.setLogoUrl(logoUrl);
		campaign.setCookieLifetime(fields.get(20).get(1).toString());
		banners.stream()
				.filter(banner -> banner.getCampaignId().equals(campaign.getId()) && banner.getType().equals("I"))
				.findFirst().ifPresent(imageBanner -> {
					campaign.setBanner(imageBanner);
				});
		campaign.setCommissionsDetails(getCommissionsByCampainId(sessionId, campaign.getCampaignId()));

		return campaign;
	}

	@Cacheable
	public List<Campaign> findByCategories(List<Long> ids, int pageNumber, int pageSize) throws UserNotFoundException {
		String url = env.getProperty("com.boostani.base.url");

		String sessionId = getAdminSessionId();
		if (StringUtils.isBlank(sessionId)) {
			throw new UserNotFoundException("Unauthorized access to Boostani Backend");
		}

		String categoriesIds = ids.stream().map(categoryIdAsString -> categoryIdAsString.toString())
				.collect(Collectors.joining(","));

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

		String listFormRequestData = env.getProperty("com.boostani.request.campain.by.category.list.form");
		String formData = String.format(listFormRequestData, "M", pageNumber, pageSize, categoriesIds, sessionId);

		map.add("D", formData);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
				getDefaultHeaders());

		ResponseEntity<List> campaignsResponse = restTemplate.postForEntity(url, request, List.class);
		Object campaignsObject = campaignsResponse.getBody().get(0);

		Campaigns campaignsList = mapper.convertValue(campaignsObject, Campaigns.class);

		List<List<String>> rows = campaignsList.getRows();
		if (rows == null || rows.isEmpty()) {
			return Collections.emptyList();
		}

		rows.remove(0);

		List<CampaignBanner> banners = getAllBanners(sessionId);

		return populateByCategories(sessionId, rows, banners);

	}
	
	protected List<Campaign> populateByCategories(String sessionId, List<List<String>> rows, List<CampaignBanner> banners) {
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
}
