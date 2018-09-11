package com.boostani.backend.api.web.account;

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

import com.boostani.backend.api.persistence.dao.AccountRepository;
import com.boostani.backend.api.persistence.model.Account;
import com.boostani.backend.api.service.EmailService;

/**
 * 
 * @author Loay
 *
 */
@Controller
@RequestMapping(path = "/account")
public class AccountController {

	private final AccountRepository accountRepository;

	private final Environment env;

	private final EmailService emailService;

	@Inject
	public AccountController(Environment env, EmailService emailService, AccountRepository accountRepository) {
		this.env = env;
		this.emailService = emailService;
		this.accountRepository = accountRepository;
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<AccountResponse> signup(@Valid @RequestBody AccountSignupForm form) {
		Account account = createAccount(form);
		
		try {
			String to = env.getProperty("com.boostani.affliate.create.email.to");
			String text = env.getProperty("com.boostani.affliate.create.email.text");

			this.emailService.sendSimpleMessage(to, "Affliate Created",
					String.format(text, form.getUsername(), form.getPassword()));

			AccountResponse response = new AccountResponse();
			response.setAccount(account);

			return new ResponseEntity<AccountResponse>(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<AccountResponse>(new AccountResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 
	 * @param form
	 * @return
	 */
	private Account createAccount(AccountSignupForm form) {
		Account account = new Account(form.getUsername(), form.getPassword(), form.getEmail(), form.getFirstName(),
				form.getLastName(), form.getBirthDate(), form.getPhoneNumber(), form.getCountry());
		accountRepository.save(account);
		return account;
	}

}
