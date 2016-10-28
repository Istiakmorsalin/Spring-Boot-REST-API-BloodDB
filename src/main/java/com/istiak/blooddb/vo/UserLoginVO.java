package com.istiak.blooddb.vo;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;


/**
 * @author Imtiaz Mirza @ imz.mrz@gmail.com
 *
 * For futurevault.sb2.ca
 */

public class UserLoginVO  {

	@NotNull(message = "Email can not be null")
	@NotEmpty(message = "Email can not be empty")

	@Email(message = "Email must be a valid format")
	private String email;

	
	@NotNull(message = "Password can not be null")
	@NotEmpty(message = "Password can not be empty")	
	private String password;
	
	public UserLoginVO() {
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