package com.istiak.blooddb.vo;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class MatchAdminLoginVO {

	
	@NotNull(message = "Email can not be null")
	@NotEmpty(message = "Email can not be empty")

	@Email(message = "Email must be a valid format")
	private String email;

	
	@NotNull(message = "Password can not be null")
	@NotEmpty(message = "Password can not be empty")	
	private String password;
	
	public MatchAdminLoginVO() {
	}
	
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email.toLowerCase();
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
