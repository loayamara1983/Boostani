package com.boostani.backend.api.web.account;

import com.boostani.backend.api.persistence.model.Account;
import com.boostani.backend.api.web.Response;

/**
 * 
 * @author Loay
 *
 */
public class AccountResponse extends Response {

	private Account account;

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

}
