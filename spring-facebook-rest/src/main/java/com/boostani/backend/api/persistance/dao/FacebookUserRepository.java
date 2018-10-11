package com.boostani.backend.api.persistance.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boostani.backend.api.persistance.model.BaseEntity.Status;
import com.boostani.backend.api.persistance.model.FacebookUser;
import com.boostani.backend.api.persistance.model.User;

public interface FacebookUserRepository extends JpaRepository<FacebookUser, String> {

	public FacebookUser findByUsername(String username);

	public FacebookUser findByEmail(String email);
}
