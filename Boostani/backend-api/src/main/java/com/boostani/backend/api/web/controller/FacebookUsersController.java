package com.boostani.backend.api.web.controller;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boostani.backend.api.service.email.EmailService;
import com.boostani.backend.api.service.user.UserAlreadyFoundException;
import com.boostani.backend.api.service.user.UserAuthenticationService;
import com.boostani.backend.api.service.user.UserCrudService;
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
		DefaultFacebookClient facebookClient = new DefaultFacebookClient(accessToken, Version.LATEST);

		User facebookUser = facebookClient.fetchObject("me", User.class,
				Parameter.with("fields", "id, name, email, first_name, last_name, birthday, picture"));

		if (facebookUser == null) {
			UserResponse userResponse = new UserResponse();
			userResponse.setMessage("Invalid user access token, we couldn't find the facebook details");
			return new ResponseEntity<>(userResponse, HttpStatus.UNAUTHORIZED);
		}

		try {
			UserResponse userResponse = register(facebookUser);
			if (userResponse == null) {
				userResponse = new UserResponse();
				userResponse.setMessage("Invalid user access token, we couldn't find the facebook details");
				return new ResponseEntity<>(userResponse, HttpStatus.UNAUTHORIZED);
			}

			if (StringUtils.isBlank(userResponse.getAccessToken())) {
				userResponse.setMessage("invalid login and/or password");
				return new ResponseEntity<>(userResponse, HttpStatus.UNAUTHORIZED);
			}

			sendCreatedUserEmail(facebookUser);

			return new ResponseEntity<>(userResponse, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(new UserResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	UserResponse login(@RequestParam("username") final String username,
			@RequestParam("password") final String password) {
		Optional<String> accessToken = authentication.login(username, password);

		if (!accessToken.isPresent()) {
			return null;
		}

		try {
			Optional<com.boostani.backend.api.persistance.model.User> currentUser = authentication
					.findByToken(accessToken.get());
			if (!currentUser.isPresent()) {
				return null;
			}

			UserResponse response = new UserResponse();

			com.boostani.backend.api.persistance.model.User user = currentUser.get();

			response.setAccessToken(accessToken.get());

			response.setUsername(user.getUsername());
			response.setFirstName(user.getFirstName());
			response.setLastName(user.getLastName());
			response.setEmail(user.getEmail());
			response.setBirthDate(user.getBirthDate());
			response.setPhoneNumber(user.getPhoneNumber());
			response.setCountry(user.getCountry());
			response.setAvatar(user.getAvatar());

			response.setMessage("User logged in");
			response.setExpiry(expiry());

			return response;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private UserResponse register(User facebookUser) throws UserAlreadyFoundException {
		com.boostani.backend.api.persistance.model.User user = com.boostani.backend.api.persistance.model.User.builder()
				.username(facebookUser.getName()).password(facebookUser.getId()).email(facebookUser.getEmail())
				.firstName(facebookUser.getFirstName()).lastName(facebookUser.getLastName())
				.birthDate(facebookUser.getBirthdayAsDate()).providerId("facebook")
				.avatar(facebookUser.getPicture() == null ? null : facebookUser.getPicture().getUrl()).build();

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
