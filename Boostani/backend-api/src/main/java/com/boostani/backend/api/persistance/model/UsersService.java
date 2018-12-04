package com.boostani.backend.api.persistance.model;

import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.boostani.backend.api.persistance.dao.UserRepository;
import com.boostani.backend.api.service.user.UserAlreadyFoundException;
import com.boostani.backend.api.service.user.UserCrudService;

@Service
final class UsersService implements UserCrudService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public User save(final User user) throws UserAlreadyFoundException {

		try {
			return userRepository.saveAndFlush(user);
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			throw new UserAlreadyFoundException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public Optional<User> find(final String id) {
		return userRepository.findById(Long.parseLong(id));
	}

	@Override
	public Optional<User> findByUsername(final String username) {
		List<User> users = userRepository.findByUsername(username);
		if (users == null || users.isEmpty()) {
			return Optional.empty();
		}
		return ofNullable(users.get(0));
	}
}
