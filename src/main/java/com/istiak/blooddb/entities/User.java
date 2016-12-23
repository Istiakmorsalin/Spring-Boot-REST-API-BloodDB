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
public class User  {

	@Id
	private String id;

	@Size(min = 2, max =50)
	@Pattern(regexp = "[A-Za-z. ]*", message = "First name requires valid character")
	private String firstname;
	
	@Size(min = 2, max = 50)
	@Pattern(regexp = "[A-Za-z. ]*", message = "Last name requires contain valid character")
	private String lastname;
	
    @Indexed( unique=true, sparse=true )
	@NotNull(message =  "Email requires valid value")
    @NotEmpty(message = "Email requires non empty value")
    @Email(message =    "Email requires valid format")
	private String email;
	

	@JsonIgnore
	@Size(min = 8, max = 48, message = "Password requires minimum 8 characters")
	private String password;

    @JsonIgnore
    private String passwordsalt = "";



	@JsonIgnore
	@SafeHtml
	private String userHash = "";

	
	@Pattern(regexp = "[A-Za-z0-9. ]*", message = "Type requires valid alphanumaric characters")
	private String type;
	private String sessionToken;
	
	@SafeHtml
	private String about;
	
	@Pattern(regexp = "[0-9.\\-+ ]*", message = "Phone requires valid alphanumaric characters")
	private String phone;
	@Pattern(regexp = "[0-9.\\-+ ]*", message = "Cell requires valid alphanumaric characters")
	private String cell ;
	
	@JsonDeserialize(using =  LocalDateTimeDeserializer.class)
	private LocalDateTime birthDate;

	@JsonDeserialize(using =  LocalDateTimeDeserializer.class)
	private LocalDateTime sessionExpireTime;
	
	@JsonDeserialize(using =  LocalDateTimeDeserializer.class)
	private LocalDateTime loginDate;
	
	@JsonDeserialize(using =  LocalDateTimeDeserializer.class)
	private LocalDateTime lastLoginTime;

	@JsonDeserialize(using =  LocalDateTimeDeserializer.class)
	private LocalDateTime joiningDate;

	@JsonDeserialize(using =  LocalDateTimeDeserializer.class)
	private LocalDateTime dateModified;


    @JsonIgnore
    private String active = "0";

	private String profileImage;

	private String bloodGroup;

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


	public String getUserHash() {
		return userHash;
	}

	public void setUserHash(String userHash) {
		this.userHash = userHash;
	}
	
	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return (firstname + ' ' + lastname).trim();
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
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

	
	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
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

	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getCell() {
		return cell;
	}


	public void setCell(String cell) {
		this.cell = cell;
	}


	public LocalDateTime getBirthDate() {
		return birthDate;
	}


	public void setBirthDate(LocalDateTime birthDate) {
		this.birthDate = birthDate;
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
}