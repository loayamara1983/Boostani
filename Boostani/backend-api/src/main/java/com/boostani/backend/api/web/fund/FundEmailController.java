package com.boostani.backend.api.web.fund;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boostani.backend.api.service.EmailService;

/**
 * 
 * @author Loay
 *
 */
@Controller
@RequestMapping(path = "/fund")
public class FundEmailController {

	private final EmailService emailService;

	@Inject
	public FundEmailController(EmailService emailService) {
		this.emailService = emailService;
	}

	@RequestMapping(value = "/transfer", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<FundResponse> doFund(@Valid @RequestBody FundEmailForm form) {
		this.emailService.sendSimpleMessage("boostanitest@gmail.com", "Fund Transfer",
				String.format("Transfer amount %s requested", form.getAmount()));
		FundResponse response = new FundResponse();
		response.setMessage("Email sent");

		return new ResponseEntity<FundResponse>(response, HttpStatus.OK);
	}

}
