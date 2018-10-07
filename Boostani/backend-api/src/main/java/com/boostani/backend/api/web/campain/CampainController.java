package com.boostani.backend.api.web.campain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@RequestMapping(path = "/campain")
public class CampainController {

	private RestTemplate restTemplate = new RestTemplate();

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

		return response.getBody().getFields().get(7).get(1);
	}

	@ApiOperation(value = "Lists the campains stored on Boostani Merchants server.", response = CampainListResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully listed all campains"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 500, message = "Internal Server error on backend server") })
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<CampainListResponse> list(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="100") int size) {
		String url = env.getProperty("com.boostani.base.url");

		CampainListResponse response = new CampainListResponse();

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

		String listFormRequestData = env.getProperty("com.boostani.request.campain.list.form");
		String formData=String.format(listFormRequestData, page, size, getSessionId());
		
		map.add("D", formData);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<List> campaignsResponse = restTemplate.postForEntity(url, request, List.class);
		Object campaignsObject = campaignsResponse.getBody().get(0);

		Campaigns campaignsList = mapper.convertValue(campaignsObject, Campaigns.class);

		List<List<String>> rows = campaignsList.getRows();
		if(rows == null || rows.isEmpty()) {
			return new ResponseEntity<CampainListResponse>(response, HttpStatus.OK);
		}
		
		rows.remove(0);

		List<Campaign> campaigns = populate(rows);
		response.setCampaigns(campaigns);

		return new ResponseEntity<CampainListResponse>(response, HttpStatus.OK);
	}

	private List<Campaign> populate(List<List<String>> rows) {
		if (rows == null || rows.isEmpty()) {
			return Collections.emptyList();
		}

		List<Campaign> campaigns = new ArrayList<>();

		for (List<String> row : rows) {
			Campaign campaign = new Campaign();

			campaign.setId(row.get(0));
			campaign.setCampaignId(row.get(1));
			campaign.setStatus(row.get(2));
			campaign.setName(row.get(3));
			campaign.setDescription(row.get(4));
			campaign.setLogoUrl(row.get(5));
			campaign.setCookieLifetime(row.get(6));
			campaign.setLongDescriptionExists(row.get(7));
			campaign.setBanners(row.get(8));
			campaign.setCommissionsExist(row.get(9));
			campaign.setCommissionsDetails(row.get(10));

			campaigns.add(campaign);
		}

		return campaigns;
	}

	@ApiOperation(value = "Displays the campain details by a given ID stored on Boostani Merchants server.", response = CampainResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully found a specific campain"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 500, message = "Internal Server error on backend server") })
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GetMapping("/{id}")
	public @ResponseBody ResponseEntity<CampainResponse> findOne(@Valid @PathVariable String id) {
		String url = env.getProperty("com.boostani.base.url");

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

		String idFormRequestData = env.getProperty("com.boostani.request.campain.id.form");
		String formData=String.format(idFormRequestData, id, getSessionId());
		
		map.add("D", formData);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<List> campaignsResponse = restTemplate.postForEntity(url, request, List.class);
		Map campaignsObject = (Map) campaignsResponse.getBody().get(0);
		List<List> fields = (List<List>) campaignsObject.get("fields");

		fields.remove(0);

		Campaign campaign = new Campaign();

		campaign.setId(fields.get(0).get(1).toString());
		campaign.setCampaignId(fields.get(1).get(1).toString());
		campaign.setStatus(fields.get(3).get(1).toString());
		campaign.setName(fields.get(4).get(1).toString());
		campaign.setDescription(fields.get(5).get(1).toString());
		campaign.setLogoUrl(fields.get(11).get(1).toString());
		campaign.setCookieLifetime(fields.get(20).get(1).toString());
		campaign.setLongDescriptionExists(fields.get(6).get(1).toString());
		campaign.setBanners(fields.get(25).get(1).toString());
		campaign.setCommissionsExist(fields.get(27).get(1).toString());

		CampainResponse response = new CampainResponse();
		response.setCampaign(campaign);

		return new ResponseEntity<CampainResponse>(response, HttpStatus.OK);
	}

}
