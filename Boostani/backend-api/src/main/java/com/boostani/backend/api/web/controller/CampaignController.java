package com.boostani.backend.api.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.boostani.backend.api.web.response.campaign.Banners;
import com.boostani.backend.api.web.response.campaign.Campaign;
import com.boostani.backend.api.web.response.campaign.CampaignBanner;
import com.boostani.backend.api.web.response.campaign.CampaignListResponse;
import com.boostani.backend.api.web.response.campaign.CampaignResponse;
import com.boostani.backend.api.web.response.campaign.Campaigns;
import com.boostani.backend.api.web.response.campaign.Login;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@RequestMapping(path = "/campaign")
public class CampaignController {

	@Autowired
	public RestTemplate restTemplate;

	private HttpHeaders headers;

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private Environment env;

	@PostConstruct
	public void setup() {
		String origin = env.getProperty("com.boostani.header.origin");
		String referer = env.getProperty("com.boostani.header.referer");

		headers = new HttpHeaders();

		headers.setAccept(Collections.singletonList(MediaType.ALL));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		headers.add("Origin", origin);
		headers.add("Accept-Encoding", "gzip, deflate");
		headers.add("Accept-Language", "en-US,en;q=0.9,ar;q=0.8");
		headers.add("Referer", referer);
	}

	public String getSessionId() {
		String username = env.getProperty("com.boostani.admin.username");
		String password = env.getProperty("com.boostani.admin.password");
		String url = env.getProperty("com.boostani.base.url");

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

		String sessionIdFormData = env.getProperty("com.boostani.request.session.id");
		String formData = String.format(sessionIdFormData, username, password, "M");
		map.add("D", formData);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<Login> response = restTemplate.postForEntity(url, request, Login.class);

		List<List<String>> fields = response.getBody().getFields();
		if (fields.size() < 8) {
			return null;
		}

		return fields.get(7).get(1);
	}

	@ApiOperation(value = "Lists the campains stored on Boostani Merchants server.", response = CampaignListResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully listed all campains"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 500, message = "Internal Server error on backend server") })
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<CampaignListResponse> list(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "100") int size) {
		String url = env.getProperty("com.boostani.base.url");

		CampaignListResponse response = new CampaignListResponse();

		String sessionId = getSessionId();
		if (StringUtils.isBlank(sessionId)) {
			response.setMessage("Unauthorized access to Boostani Backend");
			return new ResponseEntity<CampaignListResponse>(response, HttpStatus.UNAUTHORIZED);
		}

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

		String listFormRequestData = env.getProperty("com.boostani.request.campain.list.form");
		String formData = String.format(listFormRequestData, page, size, getSessionId());

		map.add("D", formData);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<List> campaignsResponse = restTemplate.postForEntity(url, request, List.class);
		Object campaignsObject = campaignsResponse.getBody().get(0);

		Campaigns campaignsList = mapper.convertValue(campaignsObject, Campaigns.class);

		List<List<String>> rows = campaignsList.getRows();
		if (rows == null || rows.isEmpty()) {
			return new ResponseEntity<CampaignListResponse>(response, HttpStatus.OK);
		}

		rows.remove(0);

		List<CampaignBanner> banners = getAllBanners(sessionId);

		List<Campaign> campaigns = populate(sessionId, rows, banners);
		response.setCampaigns(campaigns);

		return new ResponseEntity<CampaignListResponse>(response, HttpStatus.OK);
	}

	private List<CampaignBanner> getAllBanners(String sessionId) {
		String url = env.getProperty("com.boostani.base.url");
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

		String listFormRequestData = env.getProperty("com.boostani.request.campain.by.banner.list.form");
		String formData = String.format(listFormRequestData, 0, 100, sessionId);

		map.add("D", formData);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

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

	private List<Campaign> populate(String sessionId, List<List<String>> rows, List<CampaignBanner> banners) {
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
//			campaign.setLongDescriptionExists(row.get(7));

			banners.stream()
					.filter(banner -> banner.getCampaignId().equals(campaign.getId()) && banner.getType().equals("I"))
					.findFirst().ifPresent(imageBanner -> {
						campaign.setBanner(imageBanner);
					});

//			campaign.setCommissionsExist(row.get(9));

			String commissions = getCommissionsByCampainId(sessionId, campaign.getCampaignId());
			campaign.setCommissionsDetails(commissions);

			campaigns.add(campaign);
		}

		return campaigns;
	}

	private String getCommissionsByCampainId(String sessionId, String campaignId) {
		String url = env.getProperty("com.boostani.base.url");
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

		String listFormRequestData = env.getProperty("com.boostani.request.campain.by.commissions.list.form");
		String formData = String.format(listFormRequestData, campaignId, campaignId, campaignId, sessionId);

		map.add("D", formData);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

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

	@ApiOperation(value = "Displays the campain details by a given ID stored on Boostani Merchants server.", response = CampaignResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully found a specific campain"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 500, message = "Internal Server error on backend server") })
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GetMapping("/{id}")
	public @ResponseBody ResponseEntity<CampaignResponse> findOne(@Valid @PathVariable String id) {
		String url = env.getProperty("com.boostani.base.url");

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

		String idFormRequestData = env.getProperty("com.boostani.request.campain.id.form");
		String sessionId = getSessionId();

		String formData = String.format(idFormRequestData, id, id, sessionId);

		map.add("D", formData);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

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
//		campaign.setLongDescriptionExists(fields.get(6).get(1).toString());
		banners.stream()
				.filter(banner -> banner.getCampaignId().equals(campaign.getId()) && banner.getType().equals("I"))
				.findFirst().ifPresent(imageBanner -> {
					campaign.setBanner(imageBanner);
				});
//		campaign.setCommissionsExist(fields.get(27).get(1).toString());
		campaign.setCommissionsDetails(getCommissionsByCampainId(sessionId, campaign.getCampaignId()));

		CampaignResponse response = new CampaignResponse();
		response.setCampaign(campaign);

		return new ResponseEntity<CampaignResponse>(response, HttpStatus.OK);
	}

	@ApiOperation(value = "Lists the campains by categories stored on Boostani Merchants server.", response = CampaignListResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully listed all campains by categories"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 500, message = "Internal Server error on backend server") })
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/categories/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<CampaignListResponse> listByCategories(
			@RequestParam List<Long> externalCategoriesIds, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "100") int size) {
		String url = env.getProperty("com.boostani.base.url");

		CampaignListResponse response = new CampaignListResponse();

		String sessionId = getSessionId();
		if (StringUtils.isBlank(sessionId)) {
			response.setMessage("Unauthorized access to Boostani Backend");
			return new ResponseEntity<CampaignListResponse>(response, HttpStatus.UNAUTHORIZED);
		}

		String categoriesIds = externalCategoriesIds.stream().map(categoryIdAsString -> categoryIdAsString.toString())
				.collect(Collectors.joining(","));

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

		String listFormRequestData = env.getProperty("com.boostani.request.campain.by.category.list.form");
		String formData = String.format(listFormRequestData, "M", page, size, categoriesIds, getSessionId());

		map.add("D", formData);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<List> campaignsResponse = restTemplate.postForEntity(url, request, List.class);
		Object campaignsObject = campaignsResponse.getBody().get(0);

		Campaigns campaignsList = mapper.convertValue(campaignsObject, Campaigns.class);

		List<List<String>> rows = campaignsList.getRows();
		if (rows == null || rows.isEmpty()) {
			return new ResponseEntity<CampaignListResponse>(response, HttpStatus.OK);
		}

		rows.remove(0);

		List<CampaignBanner> banners = getAllBanners(sessionId);

		List<Campaign> campaigns = populate(sessionId, rows, banners);
		response.setCampaigns(campaigns);

		return new ResponseEntity<CampaignListResponse>(response, HttpStatus.OK);
	}
}
