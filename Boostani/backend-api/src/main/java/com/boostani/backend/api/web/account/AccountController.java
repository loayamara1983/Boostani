package com.boostani.backend.api.web.account;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boostani.backend.api.persistence.dao.AccountRepository;
import com.boostani.backend.api.persistence.model.Account;

/**
 * 
 * @author Loay
 *
 */
@Controller
@RequestMapping(path = "/account")
public class AccountController {

	private final AccountRepository accountRepository;

	@Inject
	public AccountController(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<AccountResponse> signup(@Valid @RequestBody AccountSignupForm form) {
		Account account = createAccount(form);
		if (account != null) {
			AccountResponse response = new AccountResponse();
			response.setAccount(account);
			
			return new ResponseEntity<AccountResponse>(response, HttpStatus.OK);
		}

		return new ResponseEntity<AccountResponse>(new AccountResponse(), HttpStatus.BAD_REQUEST);
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
