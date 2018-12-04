package com.boostani.backend.api.persistance.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boostani.backend.api.persistance.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	List<User> findByUsername(String username);

	Optional<User> findById(Long parseLong);

	User findByProviderIdAndProviderUserId(String providerId, String providerUserId);
}
