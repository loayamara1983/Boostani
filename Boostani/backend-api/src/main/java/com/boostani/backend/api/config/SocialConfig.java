package com.boostani.backend.api.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInController;

import com.boostani.backend.api.social.security.FacebookConnectionSignup;
import com.boostani.backend.api.social.security.FacebookSignInAdapter;

/**
 * 
 * @author Loay
 *
 */
@Configuration
public class SocialConfig {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private ConnectionFactoryLocator connectionFactoryLocator;

	@Autowired
	private FacebookConnectionSignup facebookConnectionSignup;

	public UsersConnectionRepository usersConnectionRepository() {
		JdbcUsersConnectionRepository usersConnectionRepository = new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
		usersConnectionRepository.setConnectionSignUp(facebookConnectionSignup);
		
		return usersConnectionRepository;
	}
	
	@Bean
	public ProviderSignInController providerSignInController() {
		return new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository(),
				new FacebookSignInAdapter());
	}
}
