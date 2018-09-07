package com.boostani.backend.api.persistence.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.boostani.backend.api.social.signup.SignupForm;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;
    
    private String imageUrl;
    
    private String profileUrl;

    private String password;
    
    private String socialPassword;
    
    private String email;
    
    private String firstName;
    
    private String lastName;
    
    private Date birthDate;
    
    private String phoneNumber;
    
    private String country;
    
    public Account() {
        super();
    }

    public Account(String username, String password, String email, String firstName, String lastName, Date birthDate,
			String phoneNumber, String country) {
		super();
		this.username = username;
		this.password = password;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDate = birthDate;
		this.phoneNumber = phoneNumber;
		this.country = country;
	}

	public Account(SignupForm form) {
		this.setUsername(form.getUsername());
		this.setSocialPassword(form.getSocialPassword());
		this.setImageUrl(form.getImageUrl());
		this.setProfileUrl(form.getProfileUrl());
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	@Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("User [id=").append(id).append(", username=").append(username).append(", password=").append(password).append("]");
        return builder.toString();
    }

    
}
