package com.boostani.backend.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableJpaRepositories("com.boostani.backend.api.persistance.dao")
@EntityScan("com.boostani.backend.api.persistance.model")
public class BoostaniApplication {

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	public static void main(String[] args) {
		SpringApplication.run(BoostaniApplication.class, args);
	}
}
