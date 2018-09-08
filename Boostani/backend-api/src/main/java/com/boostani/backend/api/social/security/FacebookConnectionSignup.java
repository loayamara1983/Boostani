package com.boostani.backend.api.social.security;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Service;

import com.boostani.backend.api.persistence.dao.AccountRepository;
import com.boostani.backend.api.persistence.model.Account;

@Service
public class FacebookConnectionSignup implements ConnectionSignUp {

	@Autowired
	private AccountRepository accountRepository;

	@Override
	public String execute(Connection<?> connection) {
		System.out.println("signup === ");
		final Account user = new Account();
		user.setUsername(connection.getDisplayName());
		user.setPassword(randomAlphabetic(8));
		user.setSocialPassword(randomAlphabetic(8));
		user.setImageUrl(connection.getImageUrl());
		user.setProfileUrl(connection.getProfileUrl());
		accountRepository.save(user);

		return user.getUsername();
	}

}
