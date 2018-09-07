package com.boostani.backend.api.web.affilate;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "/affilate")
public class AffilateController {

	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<AffilateListResponse> list() {
		AffilateListResponse response = new AffilateListResponse();

		return new ResponseEntity<AffilateListResponse>(response, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public @ResponseBody ResponseEntity<AffilateCampainsResponse> listByCampain(@Valid @PathVariable Long id, BindingResult formBinding) {
		if (formBinding.hasErrors()) {
			return null;
		}

		AffilateCampainsResponse response = new AffilateCampainsResponse();
		
		return new ResponseEntity<AffilateCampainsResponse>(response, HttpStatus.OK);
	}

}
