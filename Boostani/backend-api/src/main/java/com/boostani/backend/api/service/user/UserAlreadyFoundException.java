package com.boostani.backend.api.service.user;

@SuppressWarnings("serial")
public class UserAlreadyFoundException extends Exception {
	
    public UserAlreadyFoundException(String message) {
        super(message);
    }

    public UserAlreadyFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
