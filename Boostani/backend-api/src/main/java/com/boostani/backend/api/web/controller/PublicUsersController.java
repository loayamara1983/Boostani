package com.boostani.backend.api.web.controller;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import java.util.Date;

import javax.validation.Valid;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.boostani.backend.api.persistance.model.User;
import com.boostani.backend.api.service.EmailService;
import com.boostani.backend.api.service.UserAuthenticationService;
import com.boostani.backend.api.service.UserCrudService;
import com.boostani.backend.api.web.request.UserRegisterRequest;
import com.boostani.backend.api.web.response.user.UserResponse;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
	@PostMapping("/register")
	public @ResponseBody ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRegisterRequest request) {
		String accessToken = register(request);

		UserResponse userResponse = new UserResponse();
		try {
			userResponse.setAccessToken(accessToken);
			userResponse.setMessage("New account created successfully");
			userResponse.setExpiry(expiry());

			sendCreatedUserEmail(request);

			return new ResponseEntity<UserResponse>(userResponse, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			
			userResponse.setMessage(e.getLocalizedMessage());
			return new ResponseEntity<UserResponse>(userResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@ApiOperation(value = "login to account on Boostani by username/password", response = UserResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully logged in"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 500, message = "Internal Server error on backend server") })
	@PostMapping("/login")
	public @ResponseBody ResponseEntity<UserResponse> login(@RequestParam("username") final String username, @RequestParam("password") final String password) {
		String accessToken = authentication.login(username, password)
				.orElseThrow(() -> new RuntimeException("invalid login and/or password"));
		
		try {
			UserResponse response = new UserResponse();

			response.setAccessToken(accessToken);
			response.setMessage("User logged in");
			response.setExpiry(expiry());

			return new ResponseEntity<UserResponse>(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<UserResponse>(new UserResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private String register(UserRegisterRequest request) {
		User user = User.builder().username(request.getUsername()).password(request.getPassword())
				.email(request.getEmail()).country(request.getCountry()).firstName(request.getFirstName())
				.lastName(request.getLastName()).birthDate(request.getBirthDate()).phoneNumber(request.getPhoneNumber())
				.build();

		users.save(user);

		ResponseEntity<UserResponse> userResponse = login(request.getUsername(), request.getPassword());
		return userResponse.getBody().getAccessToken();
	}

	private void sendCreatedUserEmail(UserRegisterRequest request) throws Exception {
		String to = env.getProperty("com.boostani.affliate.create.email.to");
		String text = env.getProperty("com.boostani.affliate.create.email.text");

		this.emailService.sendSimpleMessage(to, "New User account Created",
				String.format(text, request.getUsername(), request.getPassword()));
	}

	private Date expiry() {
		String expirationSecAsString = env.getProperty("jwt.expiration-sec");

		final DateTime now = DateTime.now();
		final DateTime expiresAt = now.plusSeconds(Integer.parseInt(expirationSecAsString));
		Date expiry = expiresAt.toDate();
		
		return expiry;
	}
	
}
