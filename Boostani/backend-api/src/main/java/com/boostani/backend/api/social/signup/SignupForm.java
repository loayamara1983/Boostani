package com.boostani.backend.api.social.signup;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import java.util.Date;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.social.connect.Connection;

public class SignupForm {

	@NotEmpty
	private String username;

	@Size(min = 6, message = "must be at least 6 characters")
	private String password;
	
	private String socialPassword;
	
	private String imageUrl;
    
    private String profileUrl;

	@NotEmpty
	private String firstName;

	@NotEmpty
	private String lastName;
	
	@NotEmpty
	private String email;
    
	@NotEmpty
    private Date birthDate;
    
	@NotEmpty
    private String phoneNumber;
    
	@NotEmpty
    private String country;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}
	
	public String getSocialPassword() {
		return socialPassword;
	}

	public void setSocialPassword(String socialPassword) {
		this.socialPassword = socialPassword;
	}

	public static SignupForm fromProviderUser(Connection<?> connection) {
		SignupForm form = new SignupForm();
		
		form.setUsername(connection.getDisplayName());
		form.setImageUrl(connection.getImageUrl());
		form.setProfileUrl(connection.getProfileUrl());
		form.setSocialPassword(randomAlphabetic(8));
		
		return form;
	}
}
