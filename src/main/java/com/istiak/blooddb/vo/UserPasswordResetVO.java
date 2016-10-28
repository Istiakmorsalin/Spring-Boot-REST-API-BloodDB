package com.istiak.blooddb.vo;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by Anik on 3/23/2016.
 */
public class UserPasswordResetVO {
    @NotNull(message = "Password can not be null")
    @NotEmpty(message = "Password can not be empty")
    @Size(min = 6, max = 32, message = "password must be between 8 to 32 characters")
    private String password;
    @NotNull(message = "Password can not be null")
    @NotEmpty(message = "Password can not be empty")
    @Size(min = 6, max = 32, message = "password must be between 8 to 32 characters")
    private String newPassword;


    @NotNull(message = "Password can not be null")
    @NotEmpty(message = "Password can not be empty")
    @Size(min = 6, max = 32, message = "password must be between 8 to 32 characters")
    private String confirmPassword;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}

