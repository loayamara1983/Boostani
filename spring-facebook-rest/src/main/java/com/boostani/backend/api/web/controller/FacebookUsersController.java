package com.boostani.backend.api.web.controller;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boostani.backend.api.service.UserAuthenticationService;
import com.boostani.backend.api.service.UserCrudService;
import com.restfb.DefaultFacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.User;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/public/users/facebook")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor(access = PACKAGE)
final class FacebookUsersController {
	@NonNull
	UserAuthenticationService authentication;
	@NonNull
	UserCrudService users;

	@PostMapping("/login")
	String register(@RequestParam("accessToken") final String accessToken) {
		DefaultFacebookClient facebookClient = new DefaultFacebookClient(accessToken, Version.LATEST);

		User facebookUser = facebookClient.fetchObject("me", User.class,
				Parameter.with("fields", "id, name, email, first_name, last_name, birthday"));

		String username = facebookUser.getEmail();
		String password = facebookUser.getId();

		users.save(com.boostani.backend.api.persistance.model.User.builder().username(username).password(password)
				.email(username).firstName(facebookUser.getFirstName()).lastName(facebookUser.getLastName())
				.birthDate(facebookUser.getBirthdayAsDate()).providerId("facebook").build());

		return login(username, password);
	}

	String login(@RequestParam("username") final String username, @RequestParam("password") final String password) {
		return authentication.login(username, password)
				.orElseThrow(() -> new RuntimeException("invalid login and/or password"));
	}
}
