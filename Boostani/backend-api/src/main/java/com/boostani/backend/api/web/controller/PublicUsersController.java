package com.boostani.backend.api.web.controller;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.boostani.backend.api.persistance.model.User;
import com.boostani.backend.api.service.EmailService;
import com.boostani.backend.api.service.UserAuthenticationService;
import com.boostani.backend.api.service.UserCrudService;
import com.boostani.backend.api.web.request.UserRegisterRequest;
import com.boostani.backend.api.web.response.affilate.Affilate;
import com.boostani.backend.api.web.response.campaign.Login;
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

	@ApiOperation(value = "Creates an account on Boostani local database", response = UserResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully created user account"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 500, message = "Internal Server error on backend server") })
	@PostMapping("/register")
	public @ResponseBody ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRegisterRequest request) {
		UserResponse user = register(request);

		UserResponse userResponse = new UserResponse();
		try {
			userResponse.setAccessToken(user.getAccessToken());
			userResponse.setMessage("New account created successfully");
			userResponse.setExpiry(expiry());

			sendCreatedUserEmail(request);

			return new ResponseEntity<>(userResponse, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();

			userResponse.setMessage(e.getLocalizedMessage());
			return new ResponseEntity<>(userResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
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

			Affilate currentAffliate = getAffliateDetails(user);

			response.setAccessToken(accessToken.get());

			response.setUsername(user.getUsername());
			response.setFirstName(user.getFirstName());
			response.setLastName(user.getLastName());
			response.setEmail(user.getEmail());
			response.setBirthDate(user.getBirthDate());
			response.setPhoneNumber(user.getPhoneNumber());
			response.setCountry(user.getCountry());
			response.setAvatar(currentAffliate.getAvatar());

			response.setMessage("User logged in");
			response.setExpiry(expiry());

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(new UserResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	String getSessionId() {
		String username = env.getProperty("com.boostani.admin.username");
		String password = env.getProperty("com.boostani.admin.password");
		String url = env.getProperty("com.boostani.base.url");

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

		String sessionIdFormData = env.getProperty("com.boostani.request.session.id");
		String formData = String.format(sessionIdFormData, username, password, "M");
		map.add("D", formData);

		String origin = env.getProperty("com.boostani.header.origin");
		String referer = env.getProperty("com.boostani.header.referer");

		HttpHeaders headers = new HttpHeaders();

		headers.setAccept(Collections.singletonList(MediaType.ALL));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		headers.add("Origin", origin);
		headers.add("Accept-Encoding", "gzip, deflate");
		headers.add("Accept-Language", "en-US,en;q=0.9,ar;q=0.8");
		headers.add("Referer", referer);
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

		ResponseEntity<Login> response = restTemplate.postForEntity(url, request, Login.class);

		List<List<String>> fields = response.getBody().getFields();
		if (fields.size() < 8) {
			return null;
		}

		return fields.get(7).get(1);
	}

	private Affilate getAffliateDetails(User user) {
		String url = env.getProperty("com.boostani.base.url");
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

		String listFormRequestData = env.getProperty("com.boostani.request.campain.affiliate.get.form");
		String formData = String.format(listFormRequestData, user.getUsername(), getSessionId());

		map.add("D", formData);

		String origin = env.getProperty("com.boostani.header.origin");
		String referer = env.getProperty("com.boostani.header.referer");

		HttpHeaders headers = new HttpHeaders();

		headers.setAccept(Collections.singletonList(MediaType.ALL));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		headers.add("Origin", origin);
		headers.add("Accept-Encoding", "gzip, deflate");
		headers.add("Accept-Language", "en-US,en;q=0.9,ar;q=0.8");
		headers.add("Referer", referer);
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

		ResponseEntity<List> response = restTemplate.postForEntity(url, request, List.class);
		Map fieldsMap = (Map) response.getBody().get(0);

		List<List<String>> fields = (List) fieldsMap.get("rows");
		if (fields == null || fields.isEmpty()) {
			return null;
		}

		fields.remove(0);
		
		for (List<String> field : fields) {
			String id = field.get(1);
			//here get the affliate phote from the other service
			String username = field.get(48);
			String firstName = field.get(51);
			String lastName = field.get(52);
			String avatar = field.get(14);

			Affilate affilate = new Affilate();
			affilate.setId(id);
			affilate.setFirstName(firstName);
			affilate.setLastName(lastName);
			affilate.setUsername(username);
			affilate.setAvatar(avatar);

			return affilate;
		}

		return null;
	}

	private UserResponse register(UserRegisterRequest request) {
		User user = User.builder().username(request.getUsername()).password(request.getPassword())
				.email(request.getEmail()).country(request.getCountry()).firstName(request.getFirstName())
				.lastName(request.getLastName()).birthDate(request.getBirthDate()).phoneNumber(request.getPhoneNumber())
				.build();

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
