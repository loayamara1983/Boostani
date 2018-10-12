/**
 * 
 */
package com.boostani.backend.api.web.response.user;

import java.util.Date;

import com.boostani.backend.api.web.response.Response;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author Loay
 *
 */
public class UserResponse extends Response{

	@ApiModelProperty(notes = "Token that has to be sent on each request to the api through Authorization header")
	private String accessToken;
	
	@ApiModelProperty(notes="Displays the token expiry date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	private Date expiry;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public Date getExpiry() {
		return expiry;
	}

	public void setExpiry(Date expiry) {
		this.expiry = expiry;
	}
	
}
