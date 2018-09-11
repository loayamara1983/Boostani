package com.boostani.backend.api.web.fund;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.core.env.Environment;
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

	private final Environment env;

	private final EmailService emailService;

	@Inject
	public FundEmailController(Environment env, EmailService emailService) {
		this.env = env;
		this.emailService = emailService;
	}

	@RequestMapping(value = "/transfer", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<FundResponse> doFund(@Valid @RequestBody FundEmailForm form) {
		String to = env.getProperty("com.boostani.fund.email.to");
		String text = env.getProperty("com.boostani.fund.email.text");

		FundResponse response = new FundResponse();

		try {
			this.emailService.sendSimpleMessage(to, "Fund Transfer", String.format(text, form.getAmount()));
			response.setMessage("Email sent");

			return new ResponseEntity<FundResponse>(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage(e.getMessage());
			return new ResponseEntity<FundResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
