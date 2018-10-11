package com.boostani.backend.api.service.social;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boostani.backend.api.config.security.social.RegistrationDTO;
import com.boostani.backend.api.persistance.dao.FacebookUserRepository;
import com.boostani.backend.api.persistance.model.BaseEntity.Status;
import com.boostani.backend.api.persistance.model.FacebookUser;


@Service
public class UserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private FacebookUserRepository repository;

	@Transactional
	public FacebookUser registerNewUserAccount(RegistrationDTO userAccountData) throws DuplicateEmailException {
		LOGGER.debug("Registering new user account with information: {}", userAccountData);

		if (emailExist(userAccountData.getEmail())) {
			LOGGER.debug("Email: {} exists. Throwing exception.", userAccountData.getEmail());
			throw new DuplicateEmailException(
					"The email address: " + userAccountData.getEmail() + " is already in use.");
		}

		LOGGER.debug("Email: {} does not exist. Continuing registration.", userAccountData.getEmail());

		FacebookUser registered = new FacebookUser();
		registered.setEmail(userAccountData.getEmail());
		registered.setFirstName(userAccountData.getFirstName());
		registered.setLastName(userAccountData.getLastName());
		registered.setPassword(null);
//		registered.addRole(FacebookUser.Role.ROLE_USER_REST_MOBILE);
//		registered.setSignInProvider(userAccountData.getSignInProvider());

		LOGGER.debug("Persisting new user with information: {}", registered);

		return repository.save(registered);
	}

	private boolean emailExist(String email) {
		LOGGER.debug("Checking if email {} is already found from the database.", email);

		FacebookUser user = repository.findByEmail(email);

		if (user != null) {
			LOGGER.debug("User account: {} found with email: {}. Returning true.", user, email);
			return true;
		}

		LOGGER.debug("No user account found with email: {}. Returning false.", email);

		return false;
	}

}
