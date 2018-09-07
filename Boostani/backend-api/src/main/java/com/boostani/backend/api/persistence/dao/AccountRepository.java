package com.boostani.backend.api.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boostani.backend.api.persistence.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByUsername(final String username);

}
