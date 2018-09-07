package com.boostani.backend.api.persistence.dao;

@SuppressWarnings("serial")
public class UsernameAlreadyInUseException extends Exception {
	
	public UsernameAlreadyInUseException(String username) {
		super("The username '" + username + "' is already in use.");
	}
}
