package com.boostani.backend.api.web.controller.user;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boostani.backend.api.persistance.model.User;
import com.boostani.backend.api.service.UserAuthenticationService;
import com.boostani.backend.api.service.UserCrudService;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/public/users")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor(access = PACKAGE)
final class PublicUsersController {
	@NonNull
	UserAuthenticationService authentication;
	@NonNull
	UserCrudService users;

	@PostMapping("/register")
	String register(@RequestParam("username") final String username, @RequestParam("password") final String password) {
		users.save(User.builder().username(username).username(username).password(password).build());

		return login(username, password);
	}

	@PostMapping("/login")
	String login(@RequestParam("username") final String username, @RequestParam("password") final String password) {
		return authentication.login(username, password)
				.orElseThrow(() -> new RuntimeException("invalid login and/or password"));
	}
}
