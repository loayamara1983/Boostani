package com.boostani.backend.api.persistance.model;

import static java.util.Optional.ofNullable;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boostani.backend.api.persistance.dao.UserRepository;
import com.boostani.backend.api.service.UserCrudService;

@Service
final class UsersService implements UserCrudService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public User save(final User user) {
		Optional<User> SavedUser = findByUsername(user.getUsername());
		if (SavedUser.isPresent())
			return SavedUser.get();

		return userRepository.save(user);
	}

	@Override
	public Optional<User> find(final String id) {
		return userRepository.findById(Long.parseLong(id));
	}

	@Override
	public Optional<User> findByUsername(final String username) {
		return ofNullable(userRepository.findByUsername(username));
	}
}
