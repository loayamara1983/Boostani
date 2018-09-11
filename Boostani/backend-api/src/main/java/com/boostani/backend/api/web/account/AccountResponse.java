package com.boostani.backend.api.web.account;

import com.boostani.backend.api.persistence.model.Account;
import com.boostani.backend.api.web.Response;

import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author Loay
 *
 */
public class AccountResponse extends Response {

	@ApiModelProperty(notes = "Displays the created account on Boostani local database")
	private Account account;

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

}
