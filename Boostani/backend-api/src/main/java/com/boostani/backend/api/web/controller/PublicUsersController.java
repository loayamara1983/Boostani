package com.boostani.backend.api.web.controller;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import java.util.Date;
import java.util.Optional;

import javax.validation.Valid;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.boostani.backend.api.persistance.model.User;
import com.boostani.backend.api.service.country.CountryService;
import com.boostani.backend.api.service.email.EmailService;
import com.boostani.backend.api.service.user.UserAlreadyFoundException;
import com.boostani.backend.api.service.user.UserAuthenticationService;
import com.boostani.backend.api.service.user.UserCrudService;
import com.boostani.backend.api.service.user.UserNotFoundException;
import com.boostani.backend.api.service.user.UserService;
import com.boostani.backend.api.web.request.UserRegisterRequest;
import com.boostani.backend.api.web.response.user.Account;
import com.boostani.backend.api.web.response.user.UserResponse;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/public/users")
@FieldDefaults(level = PRIVATE)
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

	@Autowired
	public RestTemplate restTemplate;

	@Autowired
	private UserService userService;

	@Autowired
	private CountryService countryService;

	@ApiOperation(value = "Creates an account on Boostani local database", response = UserResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully created user account"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 500, message = "Internal Server error on backend server") })
	@PostMapping("/register")
	public @ResponseBody ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRegisterRequest request) {

		try {
			UserResponse userResponse = register(request);
			userResponse.setMessage("New account created successfully");

			sendCreatedUserEmail(request);
			createAffliateOnExternalResource(userResponse.getAccount());

			return new ResponseEntity<>(userResponse, HttpStatus.OK);

		} catch (UserAlreadyFoundException e) {
			e.printStackTrace();
			UserResponse response = new UserResponse();
			response.setMessage("User is already registered!!");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}catch (IncorrectResultSizeDataAccessException e) {
			e.printStackTrace();
			UserResponse response = new UserResponse();
			response.setMessage("User is already registered!!");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); 
		} catch (Exception e) {
			e.printStackTrace();
			UserResponse response = new UserResponse();
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 
	 * @param user
	 * @throws UserNotFoundException
	 */
	private void createAffliateOnExternalResource(Account account) throws UserNotFoundException {
		User user = User.builder().username(account.getUsername()).firstName(account.getFirstName())
				.lastName(account.getLastName()).country(account.getCountry()).phoneNumber(account.getPhoneNumber())
				.referralId("ref_"+account.getFirstName()).build();
		userService.createAffliate(user);
	}

	@ApiOperation(value = "login to account on Boostani by username/password", response = UserResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully logged in"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 500, message = "Internal Server error on backend server") })
	@PostMapping("/login")
	public @ResponseBody ResponseEntity<UserResponse> login(@RequestParam("username") final String username,
			@RequestParam("password") final String password) {
		UserResponse response = new UserResponse();

		Optional<String> accessToken = authentication.login(username, password);

		if (!accessToken.isPresent()) {
			response.setMessage("invalid login and/or password");
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		}

		try {
			Optional<User> currentUser = authentication.findByToken(accessToken.get());
			if (!currentUser.isPresent()) {
				response.setMessage("invalid login and/or password");
				return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
			}

			User user = currentUser.get();

			Account account = new Account();

			account.setAccessToken(accessToken.get());
			account.setExpiry(expiry());

			account.setUsername(user.getUsername());
			account.setFirstName(user.getFirstName());
			account.setLastName(user.getLastName());
			account.setEmail(user.getEmail());
			account.setReferralId(user.getReferralId());
			account.setBirthDate(user.getBirthDate());
			account.setPhoneNumber(user.getPhoneNumber());
			account.setCountry(user.getCountry());
			account.setCurrency(user.getCurrency());
			account.setAvatar(user.getAvatar());
			account.setCategories(user.getCategories());

			response.setAccount(account);
			response.setMessage("User logged in");

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			response = new UserResponse();
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private UserResponse register(UserRegisterRequest request) throws UserAlreadyFoundException {
		User user = User.builder().username(request.getUsername()).password(request.getPassword())
				.email(request.getEmail()).country(request.getCountry())
				.currency(countryService.currency(request.getCountry())).firstName(request.getFirstName())
				.lastName(request.getLastName()).birthDate(request.getBirthDate()).phoneNumber(request.getPhoneNumber())
				.referralId("refId_"+request.getFirstName()).build();

		users.save(user);

		ResponseEntity<UserResponse> userResponse = login(request.getUsername(), request.getPassword());
		return userResponse.getBody();
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
