package com.istiak.blooddb.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.istiak.blooddb.objectmappers.LocalDateTimeDeserializer;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Created by slbd on 11/22/17.
 */

@Document
public class UserPost {

    @Id
    private String id;

    private String userId;

    private String userEmail;
    private String userName;

    @SafeHtml
    @NotNull(message  = "PostBody requires valid value")
    @NotEmpty(message = "PostBody requires value")
    private String postBody;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime created;

    private String postImage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPostBody() {
        return postBody;
    }

    public void setPostBody(String postBody) {
        this.postBody = postBody;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }
}
