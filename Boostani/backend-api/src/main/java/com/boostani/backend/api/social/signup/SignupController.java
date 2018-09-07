package com.boostani.backend.api.social.signup;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import com.boostani.backend.api.persistence.dao.AccountRepository;
import com.boostani.backend.api.persistence.model.Account;
import com.boostani.backend.api.social.message.Message;
import com.boostani.backend.api.social.message.MessageType;
import com.boostani.backend.api.social.signin.SignInUtils;

@Controller
public class SignupController {

	private final AccountRepository accountRepository;
	private final ProviderSignInUtils providerSignInUtils;

	@Inject
	public SignupController(AccountRepository accountRepository, ConnectionFactoryLocator connectionFactoryLocator,
			UsersConnectionRepository connectionRepository) {
		this.accountRepository = accountRepository;
		this.providerSignInUtils = new ProviderSignInUtils(connectionFactoryLocator, connectionRepository);
	}

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public SignupForm signupForm(WebRequest request) {
		Connection<?> connection = providerSignInUtils.getConnectionFromSession(request);
		if (connection != null) {
			request.setAttribute("message", new Message(MessageType.INFO, "Your "
					+ StringUtils.capitalize(connection.getKey().getProviderId())
					+ " account is not associated with a Spring Social Showcase account. If you're new, please sign up."),
					WebRequest.SCOPE_REQUEST);

			SignupForm form = SignupForm.fromProviderUser(connection);
			
			Account account = new Account(form);
			accountRepository.save(account);
			
			return form;
		} else {
			return new SignupForm();
		}
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String signup(@Valid SignupForm form, BindingResult formBinding, WebRequest request) {
		if (formBinding.hasErrors()) {
			return null;
		}
		Account account = createAccount(form, formBinding);
		if (account != null) {
			SignInUtils.signin(account.getUsername());
			providerSignInUtils.doPostSignUp(account.getUsername(), request);
			return "redirect:/";
		}
		return null;
	}

	private Account createAccount(SignupForm form, BindingResult formBinding) {
		Account account = new Account(form.getUsername(), form.getPassword(), form.getEmail(), form.getFirstName(),
				form.getLastName(), form.getBirthDate(), form.getPhoneNumber(), form.getCountry());
		accountRepository.save(account);
		return account;
	}

}
