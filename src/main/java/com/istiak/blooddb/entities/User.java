package com.istiak.blooddb.entities;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.istiak.blooddb.objectmappers.LocalDateTimeDeserializer;



@Document
public class User implements Comparable<User> {

	@Id
	private String id;

	@Size(min = 2, max =50)
    @Pattern(regexp = "[A-Za-z. ]*", message = "Name requires valid character")
    @NotNull(message = "Name requires valid value")
    @NotEmpty(message = "Name requires non empty value")
    private String name;

    @Indexed( unique=true, sparse=true )
    @Email(message =    "Email requires valid format")
	private String email;

    @Indexed(unique = true, sparse = true)
    @Size(min = 11, max = 11)
    @Pattern(regexp = "[0-9.\\-+ ]*", message = "Phone requires valid alphanumaric characters")
    @NotNull(message = "PhoneNumber requires valid value")
    @NotEmpty(message = "PhoneNumber requires non empty value")
    private String phoneNumber;

    @NotNull(message = "BloodGroup requires valid value")
    @NotEmpty(message = "BloodGroup requires non empty value")
    private String bloodGroup;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private String passwordsalt = "";

    private String userLatitude;
    private String userLongitude;

    private String distanceFromCurrentUser;

    @JsonIgnore
    @SafeHtml
	private String userHash = "";

    private String sessionToken;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonIgnore
    private LocalDateTime sessionExpireTime;
	
	@JsonDeserialize(using =  LocalDateTimeDeserializer.class)
    @JsonIgnore
    private LocalDateTime loginDate;
	
	@JsonDeserialize(using =  LocalDateTimeDeserializer.class)
    @JsonIgnore
    private LocalDateTime lastLoginTime;

	@JsonDeserialize(using =  LocalDateTimeDeserializer.class)
    @JsonIgnore
    private LocalDateTime joiningDate;

	@JsonDeserialize(using =  LocalDateTimeDeserializer.class)
    @JsonIgnore
    private LocalDateTime dateModified;


    @JsonIgnore
    private String active = "0";

	private String profileImage;
    private String previousBloodDonationDate;
    private String userDistrict;
	private String userDivision;

    public boolean isSessionActive() {
		if(this.getSessionExpireTime() == null ){
            return false;
        }
		LocalDateTime sessionExpireTime = this.getSessionExpireTime();

		return LocalDateTime.now().isAfter(sessionExpireTime);
	}

    
    public LocalDateTime getLoginDate() {
		return loginDate;
	}
	public void setLoginDate(LocalDateTime loginDate) {
		if(loginDate !=null) {
			this.loginDate = loginDate;
		}
	}

    public String getUserLatitude() {
        return userLatitude;
    }

    public void setUserLatitude(String userLatitude) {
        this.userLatitude = userLatitude;
    }

    public String getUserLongitude() {
        return userLongitude;
    }

    public void setUserLongitude(String userLongitude) {
        this.userLongitude = userLongitude;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserHash() {
        return userHash;
	}

	public void setUserHash(String userHash) {
		this.userHash = userHash;
	}
	
	public String getId() {
		return id;
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }


	public String getPasswordsalt() {
		return passwordsalt;
	}


	public void setPasswordsalt(String passwordsalt) {
		this.passwordsalt = passwordsalt;
	}

    public String getEmail() {
        return email;
	}

	public void setEmail(String email) {
		this.email = email.toLowerCase();
	}

    public String getActive() {
        return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getSessionToken() {
		return sessionToken;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}


	public String getProfileImage() {
		return profileImage;
	}


	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}


	public LocalDateTime getSessionExpireTime() {
		return sessionExpireTime;
	}

	public void setSessionExpireTime(LocalDateTime sessionExpireTime) {
		if(sessionExpireTime !=null) {
			this.sessionExpireTime = sessionExpireTime;
		}
	}

    public String getDistanceFromCurrentUser() {
        return distanceFromCurrentUser;
    }

    public void setDistanceFromCurrentUser(String distanceFromCurrentUser) {
        this.distanceFromCurrentUser = distanceFromCurrentUser;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
	}

	public void setLastLoginTime(LocalDateTime lastLoginTime)
	{
		if(lastLoginTime !=null) {
			this.lastLoginTime = lastLoginTime;
		}
	}

	public LocalDateTime getJoiningDate() {
		return joiningDate;
	}

	public void setJoiningDate(LocalDateTime joiningDate) {
		if(joiningDate !=null) {
			this.joiningDate = joiningDate;
		}

	}

	public LocalDateTime getDateModified() {
		return dateModified;
	}

	public void setDateModified(LocalDateTime dateModified)
	{
		if(dateModified !=null) {
			this.dateModified = dateModified;
		}
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public String getPreviousBloodDonatedDate() {
		return previousBloodDonationDate;
	}

	public void setPreviousBloodDonatedDate(String previousBloodDonatedDate) {
		this.previousBloodDonationDate = previousBloodDonatedDate;
	}

	public String getUserDistrict() {
		return userDistrict;
	}

	public void setUserDistrict(String userDistrict) {
		this.userDistrict = userDistrict;
	}

	public String getUserDivision() {
		return userDivision;
	}

	public void setUserDivision(String userDivision) {
		this.userDivision = userDivision;
	}

    @Override
    public int compareTo(User o) {
        return new String(this.distanceFromCurrentUser).compareTo(new String(o.distanceFromCurrentUser));
    }

}