package com.boostani.backend.api.web.controller;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.boostani.backend.api.persistance.model.User;
import com.boostani.backend.api.service.storage.FileStorageService;
import com.boostani.backend.api.service.user.UserAlreadyFoundException;
import com.boostani.backend.api.service.user.UserAuthenticationService;
import com.boostani.backend.api.service.user.UserCrudService;
import com.boostani.backend.api.web.request.CategoryListRequest;
import com.boostani.backend.api.web.response.category.CategoryResponse;
import com.boostani.backend.api.web.response.user.Account;
import com.boostani.backend.api.web.response.user.UploadFileResponse;
import com.boostani.backend.api.web.response.user.UserResponse;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

/**
 * 
 * @author Loay
 *
 */
@RestController
@RequestMapping("/users")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor(access = PACKAGE)
final class SecuredUsersController {

	@NonNull
	UserAuthenticationService authentication;

	@NonNull
	UserCrudService users;

	@Autowired
	private FileStorageService fileStorageService;

	@GetMapping("/current")
	User getCurrent(@AuthenticationPrincipal final User user) {
		return user;
	}

	@PostMapping("/uploadFile")
	public UploadFileResponse uploadFile(@AuthenticationPrincipal final User user,
			@RequestParam("file") MultipartFile file) throws UserAlreadyFoundException {
		String fileName = fileStorageService.storeFile(user, file);

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/downloadFile/")
				.path(fileName).toUriString();

		user.setAvatar(fileDownloadUri);
		users.save(user);

		return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
	}

	@GetMapping("/downloadFile/{fileName:.+}")
	public ResponseEntity<byte[]> downloadFile(@AuthenticationPrincipal final User user, @PathVariable String fileName,
			HttpServletRequest request) {

		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.noCache().getHeaderValue());

		byte[] media = user.getProfileImage();

		ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(media, headers, HttpStatus.OK);
		return responseEntity;
	}
	
	@ApiOperation(value = "Saves categories to this user", response = CategoryResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully saved categories"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 500, message = "Internal Server error on backend server") })
	@RequestMapping(value = "/saveCategories", method = RequestMethod.POST, headers = "Accept=application/json", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<UserResponse> saveCategories(@AuthenticationPrincipal final User user, @RequestBody CategoryListRequest categories) {
		
		UserResponse response = new UserResponse();
		
		try {
			user.setCategories(categories.getCategories());
			users.save(user);
			
			Account account = new Account();

			account.setUsername(user.getUsername());
			account.setFirstName(user.getFirstName());
			account.setLastName(user.getLastName());
			account.setEmail(user.getEmail());
			account.setBirthDate(user.getBirthDate());
			account.setPhoneNumber(user.getPhoneNumber());
			account.setCountry(user.getCountry());
			account.setAvatar(user.getAvatar());
			account.setCategories(user.getCategories());
			
			response.setAccount(account);
			
			return new ResponseEntity<>(response, HttpStatus.OK);
			
		} catch (Exception e) {
			e.printStackTrace();
			response = new UserResponse();
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@GetMapping("/logout")
	boolean logout(@AuthenticationPrincipal final User user) {
		authentication.logout(user);
		return true;
	}
}
