package com.istiak.blooddb.vo;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by Anik on 3/23/2016.
 */
public class ResetPasswordVO {
    private String primaryEmail;
    @NotEmpty(message = "hash can not be empty")
    private String hash;
    @NotNull(message = "Password can not be null")
    @NotEmpty(message = "Password can not be empty")
    @Size(min = 6, max = 32, message = "Password must be six character long")
    private String password;
    @Size(min = 6, max = 32, message = "Password must be six character long")
    private String confirmPassword;

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
