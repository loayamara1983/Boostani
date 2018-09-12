package com.boostani.backend.api.web.affilate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.boostani.backend.api.persistence.dao.AccountRepository;
import com.boostani.backend.api.web.campain.Campaign;
import com.boostani.backend.api.web.campain.Campaigns;
import com.boostani.backend.api.web.campain.Login;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@RequestMapping(path = "/affilate")
public class AffilateController {

	private RestTemplate restTemplate = new RestTemplate();

	private final String url = "http://boostini.postaffiliatepro.com/scripts/server.php";
	private final String username = "alaa.nobani1982@gmail.com";
	private final String password = "alaanobani1982";

	private HttpHeaders headers;

	private ObjectMapper mapper = new ObjectMapper();
	
    @Autowired
    private AccountRepository accountRepository;
    
	@Autowired
	private Environment env;
	
	@PostConstruct
	public void setup() {
		headers = new HttpHeaders();

		headers.setAccept(Collections.singletonList(MediaType.ALL));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		headers.add("Origin", "http://boostini.postaffiliatepro.com");
		headers.add("Accept-Encoding", "gzip, deflate");
		headers.add("Accept-Language", "en-US,en;q=0.9,ar;q=0.8");
		headers.add("Referer", "http://boostini.postaffiliatepro.com/affiliates/panel.php");
	}

	public String getSessionId() {
		/*final Account user = accountRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        
        String name=user.getEmail();
        String password=user.getPassword();*/
        
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

		String sessionIdFormData = env.getProperty("com.boostani.request.session.id");
		String formData = String.format(sessionIdFormData, username, password, "A");
		map.add("D", formData);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<Login> response = restTemplate.postForEntity(url, request, Login.class);

		return response.getBody().getFields().get(7).get(1);
	}

	@ApiOperation(value = "Lists the affiliates and names of private (public with manual approval) campaigns the affiliates belong to.", response = AffilateListResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully created account"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 500, message = "Internal Server error on backend server") })
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/campains", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<AffilateListResponse> listCampains(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="100") int size) {
		
		AffilateListResponse response = new AffilateListResponse();

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

		String listFormRequestData = env.getProperty("com.boostani.request.affiliate.campain.list.form");
		String formData=String.format(listFormRequestData, page, size, getSessionId());
		map.add("D", formData);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<List> campaignsResponse = restTemplate.postForEntity(url, request, List.class);
		Object campaignsObject = campaignsResponse.getBody().get(0);

		Campaigns campaignsList = mapper.convertValue(campaignsObject, Campaigns.class);

		List<List<String>> rows = campaignsList.getRows();
		if(rows == null || rows.isEmpty()) {
			return new ResponseEntity<AffilateListResponse>(response, HttpStatus.OK);
		}
		
		rows.remove(0);

		List<Campaign> campaigns = populate(rows);
		response.setCampaigns(campaigns);

		return new ResponseEntity<AffilateListResponse>(response, HttpStatus.OK);
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

}
