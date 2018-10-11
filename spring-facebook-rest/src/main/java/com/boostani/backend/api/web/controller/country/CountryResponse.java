package com.boostani.backend.api.web.controller.country;

import java.util.List;

import com.boostani.backend.api.web.controller.user.Response;

import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author Loay
 *
 */
public class CountryResponse extends Response{

	@ApiModelProperty(notes = "Displays the all avaliable countries")
	private List<Country> countries;

	public List<Country> getCountries() {
		return countries;
	}

	public void setCountries(List<Country> countries) {
		this.countries = countries;
	}
}
