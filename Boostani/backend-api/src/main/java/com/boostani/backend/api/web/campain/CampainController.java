package com.boostani.backend.api.web.campain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping(path = "/campain")
public class CampainController {

	private RestTemplate restTemplate = new RestTemplate();

	private final String url = "http://boostini.postaffiliatepro.com/scripts/server.php";
	private final String adminUsername = "ta.na.mails@gmail.com";
	private final String adminPassword = "B00stini76*#";

	private HttpHeaders headers;

	private ObjectMapper mapper = new ObjectMapper();

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
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

		String formData = "{\"C\":\"Pap_Api_AuthService\",\"M\":\"authenticate\",\"fields\":[[\"name\",\"value\",\"values\",\"error\"],["
				+ "\"username\",\"" + adminUsername + "\",null,\"\"],[" + "\"password\",\"" + adminPassword
				+ "\",null,\"\"],[\"roleType\",\"M\",null,\"\"],[\"isFromApi\",\"Y\",null,\"\"],[\"apiVersion\",\"c278cce45ba296bc421269bfb3ddff74\",null,\"\"]]}";
		map.add("D", formData);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<Login> response = restTemplate.postForEntity(url, request, Login.class);

		return response.getBody().getFields().get(7).get(1);
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<CampainListResponse> list() {
		CampainListResponse response = new CampainListResponse();

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

		String formData = "{\"C\":\"Gpf_Rpc_Server\", \"M\":\"run\", \"requests\":[{\"C\":\"Pap_Affiliates_Promo_CampaignsGrid\", \"M\":\"getRows\", \"offset\":0, \"limit\":100, \"columns\":[[\"id\"],[\"id\"],[\"name\"],[\"description\"],[\"logourl\"],[\"banners\"],[\"longdescriptionexists\"],[\"commissionsdetails\"],[\"rstatus\"],[\"commissionsexist\"]]}], \"S\":\""
				+ getSessionId() + "\"}";
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GetMapping("/{id}")
	public @ResponseBody ResponseEntity<CampainResponse> findOne(@Valid @PathVariable String id) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

		String formData = "{\"C\":\"Gpf_Rpc_Server\", \"M\":\"run\", \"requests\":[{\"C\":\"Pap_Merchants_Campaign_CampaignForm\", \"M\":\"load\", \"fields\":[[\"name\",\"value\"],[\"Id\",\"00f148b3\"]]},{\"C\":\"Pap_Merchants_Campaign_CampaignDetailsAdditionalForm\", \"M\":\"getFields\", \"fieldParam\":\"\"},{\"C\":\"Pap_Merchants_Campaign_CampaignDetailsAdditionalForm\", \"M\":\"load\", \"fields\":[[\"name\",\"value\"],[\"Id\","
				+ "\"" + id + "\"]]}], \"S\":" + "\"" + getSessionId() + "\"}";
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
