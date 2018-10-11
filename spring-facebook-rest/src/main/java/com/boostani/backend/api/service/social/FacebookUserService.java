package com.boostani.backend.api.service.social;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boostani.backend.api.persistance.dao.FacebookUserRepository;
import com.boostani.backend.api.persistance.dao.UserRepository;
import com.boostani.backend.api.persistance.model.FacebookUser;
import com.boostani.backend.api.persistance.model.User;

@Service
@Transactional
public class FacebookUserService implements SocialUserService {

	@Autowired
	private FacebookUserRepository userRepo;

	private final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();

	@Override
	@Transactional(readOnly = true)
	public FacebookUser loadUserByUserId(String userId)  {
		final FacebookUser user = userRepo.findOne(userId);
		return checkUser(user);
	}

	@Override
	@Transactional(readOnly = true)
	public FacebookUser loadUserByUsername(String username) {
		final FacebookUser user = userRepo.findByUsername(username);
		return checkUser(user);
	}

	@Override
	@Transactional(readOnly = true)
	public FacebookUser loadUserByConnectionKey(ConnectionKey connectionKey) {
//		final FacebookUser user = userRepo.findByProviderIdAndProviderUserId(connectionKey.getProviderId(), connectionKey.getProviderUserId());
//		return checkUser(user);
		return null;
	}

	@Override
	public void updateUserDetails(FacebookUser user) {
		userRepo.save(user);
	}

	private FacebookUser checkUser(FacebookUser user) {
		if (user == null) {
			throw new UsernameNotFoundException("user not found");
		}
		detailsChecker.check(user);
		return user;
	}
}
