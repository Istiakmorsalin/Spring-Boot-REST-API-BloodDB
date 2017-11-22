package com.istiak.blooddb.services;

import com.istiak.blooddb.dao.SpringDataDBUtils;
import com.istiak.blooddb.entities.UserPost;
import com.istiak.blooddb.utils.AppConstant;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by slbd on 11/22/17.
 */
@Service
@Repository
public class UserPostService {

    public Object create(UserPost userPost) throws Exception {
        userPost.setCreated(LocalDateTime.now());
        SpringDataDBUtils.getMongoOperations().insert(userPost);
        return userPost;
    }

    public UserPost get(String userPostId) throws Exception {

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(userPostId));

        return SpringDataDBUtils.getMongoOperations().findOne(query,
                UserPost.class);
    }

    public UserPost update(UserPost userPost) throws Exception {

        userPost.setCreated(LocalDateTime.now());
        SpringDataDBUtils.getMongoOperations().save(userPost);

        return userPost;
    }


    public List<UserPost> listByuserEmail( String userEmail) throws Exception {

        List<UserPost> userPost = new ArrayList<UserPost>();
        Query query = new Query();
        query.addCriteria(Criteria.where("userEmail").is(userEmail));
        userPost = SpringDataDBUtils.getMongoOperations().find(query, UserPost.class);

        return userPost;
    }

    public List<UserPost> listByUserId( String userId) throws Exception {

        List<UserPost> userPost = new ArrayList<UserPost>();
        Query query = new Query();

        query.addCriteria(Criteria.where("userId").is(userId));
        userPost = SpringDataDBUtils.getMongoOperations().find(query, UserPost.class);

        return userPost;
    }
}
