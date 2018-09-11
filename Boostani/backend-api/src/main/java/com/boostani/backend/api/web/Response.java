package com.boostani.backend.api.web;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author Loay
 *
 */
public class Response {

	@ApiModelProperty(notes = "Displays the response message after request has been submitted")
	private String message = "success";

	@ApiModelProperty(notes = "Displays backend server local time")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	private Date time = new Date();

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getTime() {
		return time;
	}
	
}
