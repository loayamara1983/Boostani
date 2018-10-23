package com.boostani.backend.api.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boostani.backend.api.persistance.model.User;
import com.boostani.backend.api.service.affiliate.AffliateService;
import com.boostani.backend.api.service.user.UserNotFoundException;
import com.boostani.backend.api.web.response.affilate.AffilateListResponse;
import com.boostani.backend.api.web.response.campaign.Campaign;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@RequestMapping(path = "/affilates")
public class AffilateCampaignsController {

	@Autowired
	private AffliateService affliateService;
	
	@ApiOperation(value = "Lists the affiliates and names of private (public with manual approval) campaigns the affiliates belong to.", response = AffilateListResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully listed all campains"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 500, message = "Internal Server error on backend server") })
	@RequestMapping(value = "/campaings/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<AffilateListResponse> listCampains(
			@AuthenticationPrincipal final User currentUser, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "100") int size) {

		AffilateListResponse response = new AffilateListResponse();

		try {
			List<Campaign> campaigns = affliateService.findCampains(currentUser, page, size);
			
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

}
