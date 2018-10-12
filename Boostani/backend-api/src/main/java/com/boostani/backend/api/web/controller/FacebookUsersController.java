package com.boostani.backend.api.web.controller;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import java.util.Date;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boostani.backend.api.service.EmailService;
import com.boostani.backend.api.service.UserAuthenticationService;
import com.boostani.backend.api.service.UserCrudService;
import com.boostani.backend.api.web.response.user.UserResponse;
import com.restfb.DefaultFacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.User;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
	
	@Autowired
	private Environment env;

	@Autowired
	private EmailService emailService;

	@ApiOperation(value = "Creates an account on Boostani local database", response = UserResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully created user account"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 500, message = "Internal Server error on backend server") })
	@PostMapping("/login")
	private ResponseEntity<UserResponse> autoRegisterByLogin(@RequestParam("accessToken") final String accessToken) {
		UserResponse userResponse = new UserResponse();

		DefaultFacebookClient facebookClient = new DefaultFacebookClient(accessToken, Version.LATEST);

		User facebookUser = facebookClient.fetchObject("me", User.class,
				Parameter.with("fields", "id, name, email, first_name, last_name, birthday"));
		
		if(facebookUser == null) {
			userResponse.setMessage("Invalid user access token, we couldn't find the facebook details");
			return new ResponseEntity<UserResponse>(userResponse, HttpStatus.UNAUTHORIZED);
		}
		
		try {
			String token = register(facebookUser);
			
			userResponse.setAccessToken(token);
			userResponse.setMessage("User account registeration and login done properly");
			userResponse.setExpiry(expiry());

			sendCreatedUserEmail(facebookUser);

			return new ResponseEntity<UserResponse>(userResponse, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			
			userResponse.setMessage(e.getLocalizedMessage());
			return new ResponseEntity<UserResponse>(userResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	String login(@RequestParam("username") final String username, @RequestParam("password") final String password) {
		return authentication.login(username, password)
				.orElseThrow(() -> new RuntimeException("invalid login and/or password"));
	}
	
	private String register(User facebookUser) {
		com.boostani.backend.api.persistance.model.User user = com.boostani.backend.api.persistance.model.User.builder().username(facebookUser.getName()).password(facebookUser.getId())
				.email(facebookUser.getEmail()).firstName(facebookUser.getFirstName())
				.lastName(facebookUser.getLastName()).birthDate(facebookUser.getBirthdayAsDate()).providerId("facebook")
				.build();

		users.save(user);

		return login(facebookUser.getName(), facebookUser.getId());
	}
	
	private void sendCreatedUserEmail(User facebookUser) throws Exception {
		String to = env.getProperty("com.boostani.affliate.create.email.to");
		String text = env.getProperty("com.boostani.affliate.create.email.text");

		this.emailService.sendSimpleMessage(to, "New User account Created by facebook",
				String.format(text, facebookUser.getName(), facebookUser.getId()));
	}
	
	private Date expiry() {
		String expirationSecAsString = env.getProperty("jwt.expiration-sec");

		final DateTime now = DateTime.now();
		final DateTime expiresAt = now.plusSeconds(Integer.parseInt(expirationSecAsString));
		Date expiry = expiresAt.toDate();
		
		return expiry;
	}
}
