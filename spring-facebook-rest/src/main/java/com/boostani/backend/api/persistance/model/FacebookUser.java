package com.boostani.backend.api.persistance.model;

import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.social.security.SocialUserDetails;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "facebook_user")
public class FacebookUser implements SocialUserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3204948850973858185L;

	/**
	 * 
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	// @NotNull
	@JsonIgnore
	private String providerId;

	// @NotNull
	@JsonIgnore
	private String providerUserId;

	// @NotNull
	@JsonIgnore
	private String accessToken;

	@NotNull
	@Size(min = 4, max = 30)
	private String username;

	private String password;

	private String firstName;

	// @NotEmpty
	private String lastName;

	private String email;

	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING, timezone = "CET")
	private Date birthDate;

	private String phoneNumber;

	private String country;

	@Transient
	private long expires;

	@NotNull
	private boolean accountExpired;

	@NotNull
	private boolean accountLocked;

	@NotNull
	private boolean credentialsExpired;

	@NotNull
	private boolean accountEnabled;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = true)
	private Set<UserAuthority> authorities;

	public FacebookUser() {
		super();
	}

	public FacebookUser(Long id, String providerId, String providerUserId, String accessToken, String username, String password,
			String firstName, String lastName, String email, Date birthDate, String phoneNumber, String country,
			long expires, boolean accountExpired, boolean accountLocked, boolean credentialsExpired,
			boolean accountEnabled, Set<UserAuthority> authorities) {
		super();
		this.id = id;
		this.providerId = providerId;
		this.providerUserId = providerUserId;
		this.accessToken = accessToken;
		this.username = username;
		this.setPassword(password);
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.birthDate = birthDate;
		this.phoneNumber = phoneNumber;
		this.country = country;
		this.expires = expires;
		this.accountExpired = accountExpired;
		this.accountLocked = accountLocked;
		this.credentialsExpired = credentialsExpired;
		this.accountEnabled = accountEnabled;
		this.authorities = authorities;
	}

	public Long getId() {
		return id;
	}

	@JsonIgnore
	public String getUserId() {
		return id.toString();
	}

	public String getUsername() {
		return username;
	}

	@JsonIgnore
	public Set<UserAuthority> getAuthorities() {
		return authorities;
	}

	// Use Roles as external API
	public Set<UserRole> getRoles() {
		Set<UserRole> roles = EnumSet.noneOf(UserRole.class);
		if (authorities != null) {
			for (UserAuthority authority : authorities) {
				roles.add(UserRole.valueOf(authority));
			}
		}
		return roles;
	}
	
	@JsonIgnore
	public boolean isAccountNonExpired() {
		return !accountExpired;
	}

	@JsonIgnore
	public boolean isAccountNonLocked() {
		return !accountLocked;
	}

	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return !credentialsExpired;
	}

	@JsonIgnore
	public boolean isEnabled() {
		return !accountEnabled;
	}

	public long getExpires() {
		return expires;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + getUsername();
	}

	public String getProviderId() {
		return providerId;
	}

	public String getProviderUserId() {
		return providerUserId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getCountry() {
		return country;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public void setExpires(long expires) {
		this.expires = expires;
	}

	public boolean isAccountExpired() {
		return accountExpired;
	}

	public void setAccountExpired(boolean accountExpired) {
		this.accountExpired = accountExpired;
	}

	public boolean isAccountLocked() {
		return accountLocked;
	}

	public void setAccountLocked(boolean accountLocked) {
		this.accountLocked = accountLocked;
	}

	public boolean isCredentialsExpired() {
		return credentialsExpired;
	}

	public void setCredentialsExpired(boolean credentialsExpired) {
		this.credentialsExpired = credentialsExpired;
	}

	public boolean isAccountEnabled() {
		return accountEnabled;
	}

	public void setAccountEnabled(boolean accountEnabled) {
		this.accountEnabled = accountEnabled;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public void setProviderUserId(String providerUserId) {
		this.providerUserId = providerUserId;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setAuthorities(Set<UserAuthority> authorities) {
		this.authorities = authorities;
	}

	
}
