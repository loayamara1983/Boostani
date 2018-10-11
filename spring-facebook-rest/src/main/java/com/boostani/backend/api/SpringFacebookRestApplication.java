package com.boostani.backend.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.boostani.backend.api.persistance.dao")
@EntityScan("com.boostani.backend.api.persistance.model")
public class SpringFacebookRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringFacebookRestApplication.class, args);
	}
}
