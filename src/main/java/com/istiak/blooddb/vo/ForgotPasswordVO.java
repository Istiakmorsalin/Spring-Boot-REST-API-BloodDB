package com.istiak.blooddb.vo;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by Anik on 3/23/2016.
 */
public class ForgotPasswordVO {

    @NotEmpty(message = "Email can not be empty")
    @Email(message = "Email must be a valid format")
    private String primaryEmail;

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }
}
