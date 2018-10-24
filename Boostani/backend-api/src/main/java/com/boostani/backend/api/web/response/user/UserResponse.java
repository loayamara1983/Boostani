/**
 * 
 */
package com.boostani.backend.api.web.response.user;

import com.boostani.backend.api.web.response.Response;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Loay
 *
 */
@JsonInclude(Include.NON_NULL)
public class UserResponse extends Response {

	private Account account;

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

}
