package com.boostani.backend.api.config.security.social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.boostani.backend.api.persistance.dao.UserRepository;
import com.boostani.backend.api.persistance.model.User;
import com.boostani.backend.api.persistance.model.UserRole;

@Component
public class AutoSignUpHandler implements ConnectionSignUp {

	@Autowired
	UserRepository userRepository;

	private volatile long userCount;

	@Override
	@Transactional
	public String execute(final Connection<?> connection) {
		// add new users to the db with its default roles for later use in
		// SocialAuthenticationSuccessHandler
		final User user = User.builder().username(generateUniqueUserName(connection.getDisplayName()))
				.providerId(connection.getKey().getProviderId()).providerUserId(connection.getKey().getProviderUserId())
				.accessToken(connection.createData().getAccessToken()).build();

		grantRoles(user);
		userRepository.save(user);
		return user.getUserId();
	}

	private void grantRoles(final User user) {
		user.grantRole(UserRole.USER);

		// grant admin rights to the first user
		if (userCount == 0) {
			userCount = userRepository.count();
			if (userCount == 0) {
				user.grantRole(UserRole.ADMIN);
			}
		}
	}

	private String generateUniqueUserName(final String firstName) {
		String username = getUsernameFromFirstName(firstName);
		String option = username;
		for (int i = 0; userRepository.findByUsername(option) != null; i++) {
			option = username + i;
		}
		return option;
	}

	private String getUsernameFromFirstName(final String userId) {
		final int max = 25;
		int index = userId.indexOf(' ');
		index = index == -1 || index > max ? userId.length() : index;
		index = index > max ? max : index;
		return userId.substring(0, index);
	}
}
