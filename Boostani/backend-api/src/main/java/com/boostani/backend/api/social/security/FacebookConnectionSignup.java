package com.boostani.backend.api.social.security;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Service;

import com.boostani.backend.api.persistence.dao.AccountRepository;
import com.boostani.backend.api.persistence.model.Account;
import com.boostani.backend.api.service.EmailService;

@Service
public class FacebookConnectionSignup implements ConnectionSignUp {

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private Environment env;

	@Autowired
	private EmailService emailService;

	@Override
	public String execute(Connection<?> connection) {
		System.out.println("signup === ");
		
		final Account user = new Account();
		
		String username = connection.getDisplayName();
		String password = randomAlphabetic(8);
		
		user.setUsername(username);
		user.setPassword(password);
		user.setSocialPassword(password);
		user.setImageUrl(connection.getImageUrl());
		user.setProfileUrl(connection.getProfileUrl());
		
		accountRepository.save(user);
		
		String to = env.getProperty("com.boostani.affliate.create.email.to");
		String text = env.getProperty("com.boostani.affliate.create.email.text");

		try {
			this.emailService.sendSimpleMessage(to, "Affliate Created",
					String.format(text, username, password));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return username;
	}

}
