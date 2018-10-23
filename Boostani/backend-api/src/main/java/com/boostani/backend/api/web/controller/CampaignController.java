package com.boostani.backend.api.web.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boostani.backend.api.service.campaign.CampaignService;
import com.boostani.backend.api.service.user.UserNotFoundException;
import com.boostani.backend.api.web.response.campaign.Campaign;
import com.boostani.backend.api.web.response.campaign.CampaignListResponse;
import com.boostani.backend.api.web.response.campaign.CampaignResponse;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@RequestMapping(path = "/campaign")
public class CampaignController {

	@Autowired
	private CampaignService campaignService;

	@ApiOperation(value = "Lists the campains stored on Boostani Merchants server.", response = CampaignListResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully listed all campains"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 500, message = "Internal Server error on backend server") })
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<CampaignListResponse> list(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "100") int size) {

		CampaignListResponse response = new CampaignListResponse();

		try {
			List<Campaign> campaigns = campaignService.findAll(page, size);

			response.setCampaigns(campaigns);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (UserNotFoundException e) {
			e.printStackTrace();
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@ApiOperation(value = "Displays the campain details by a given ID stored on Boostani Merchants server.", response = CampaignResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully found a specific campain"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 500, message = "Internal Server error on backend server") })
	@GetMapping("/{id}")
	public @ResponseBody ResponseEntity<CampaignResponse> findOne(@Valid @PathVariable String id) {

		CampaignResponse response = new CampaignResponse();

		try {
			Campaign campaign = campaignService.findOne(id);

			response.setCampaign(campaign);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (UserNotFoundException e) {
			e.printStackTrace();
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@ApiOperation(value = "Lists the campains by categories stored on Boostani Merchants server.", response = CampaignListResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully listed all campains by categories"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 500, message = "Internal Server error on backend server") })
	@RequestMapping(value = "/categories/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<CampaignListResponse> listByCategories(
			@RequestParam List<Long> externalCategoriesIds, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "100") int size) {

		CampaignListResponse response = new CampaignListResponse();

		List<Campaign> campaigns;
		try {
			campaigns = campaignService.findByCategories(externalCategoriesIds, page, size);

			response.setCampaigns(campaigns);
			return new ResponseEntity<CampaignListResponse>(response, HttpStatus.OK);

		} catch (UserNotFoundException e) {
			e.printStackTrace();
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
