package com.boostani.backend.api.config.security.social;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Service;

import com.boostani.backend.api.persistance.dao.FacebookUserRepository;
import com.boostani.backend.api.persistance.model.BaseEntity.Status;
import com.boostani.backend.api.persistance.model.FacebookUser;

@Service
public class SimpleSocialUserDetailsService implements SocialUserDetailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSocialUserDetailsService.class);

	@Autowired
	private FacebookUserRepository repository;

	@Override
	public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException, DataAccessException {
		LOGGER.debug("Loading user by user id: {}", userId);

		FacebookUser user = repository.findByEmail(userId);
		LOGGER.debug("Found user: {}", user);

		if (user == null) {
			throw new UsernameNotFoundException("No user found with username: " + userId);
		}

		FacebookTokenUserDetails principal = new FacebookTokenUserDetails(user.getEmail(), user.getPassword(),
				user.getAuthorities());
		principal.setFirstName(user.getFirstName());
//		principal.setId(user.getId());
		principal.setLastName(user.getLastName());
//		principal.setSocialSignInProvider(user.getSignInProvider());

		LOGGER.debug("Found user details: {}", principal);

		return principal;
	}
}
