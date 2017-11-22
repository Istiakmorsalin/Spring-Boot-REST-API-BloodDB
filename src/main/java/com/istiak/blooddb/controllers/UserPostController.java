package com.istiak.blooddb.controllers;

import com.google.gson.Gson;
import com.istiak.blooddb.entities.UserPost;
import com.istiak.blooddb.services.UserPostService;
import com.istiak.blooddb.utils.AppConstant;
import com.istiak.blooddb.utils.BDUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by slbd on 11/22/17.
 */
@Path("/userposts")
public class UserPostController {

    private static final Logger logger = LoggerFactory.getLogger(UserPostController.class);
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    Gson gson = new Gson();
    @Autowired
    private UserPostService userPostService;

    /******************************* Create **********************************/

    /**
     *
     *
     *
     */
    @POST
    @Path("/picture")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("application/json")
    public Response createUserPostWithImage(
            @FormDataParam("userId") String userId,
            @FormDataParam("userEmail") String userEmail,
            @FormDataParam("postBody") String postBody,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {


        OutputStream os = null;
        UserPost userPost = new UserPost();

        Map<Object, Object> apiResponse = new HashMap<Object, Object>();
        Map<Object, Object> serviceResponse = new HashMap<Object, Object>();

        try {

            userPost.setUserId(userId);
            userPost.setUserEmail(userEmail);
            userPost.setPostBody(postBody);

            Set<ConstraintViolation<UserPost>> validateErrors = validator
                    .validate(userPost);


            if (validateErrors.isEmpty()) {


                logger.info(" User id" + userPost.getUserId());
                logger.info(AppConstant.BLOG_PIC_DIR);
                userPost.setPostImage(AppConstant.BLOG_PIC_DIR + File.separator + fileDetail.getFileName());
                logger.info(" ProfileImagePath" + userPost.getPostImage());

                userPost = (UserPost) userPostService
                        .create(userPost);

                File fileToUpload = new File(AppConstant.BLOG_PIC_DIR + File.separator + userPost.getId() + fileDetail.getFileName());
                os = new FileOutputStream(fileToUpload);
                byte[] b = new byte[2048];
                int length;
                while ((length = uploadedInputStream.read(b)) != -1) {
                    os.write(b, 0, length);
                }

                if (userPost != null) {
                    apiResponse.put("apiresponse", userPost);
                    return Response.ok(apiResponse).build();
                }
            } else {
                for (ConstraintViolation<UserPost> error : validateErrors) {
                    apiResponse.put(error.getPropertyPath().toString(),
                            error.getMessage());
                }
                return Response.status(400).entity(apiResponse).build();
            }
        } catch (Exception e) {

            logger.error(e.getMessage());
            apiResponse.put("error", e.getMessage());
        } finally {
            try {
                os.close();
            } catch (IOException ex) {

                logger.error(ex.getMessage());
                apiResponse.put("error", ex.getMessage());

                return Response.status(500).entity(apiResponse).build();
            }
        }
        return Response.status(500).entity(apiResponse).build();
    }


    /******************************* Create **********************************/
    /**
     *
     *
     *
     */
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response createUserPost(UserPost userPost) {

        Map<Object, Object> apiResponse = new HashMap<Object, Object>();
        Map<Object, Object> serviceResponse = new HashMap<Object, Object>();

        try {
            Set<ConstraintViolation<UserPost>> validateErrors = validator
                    .validate(userPost);


            if (validateErrors.isEmpty()) {
                userPost = (UserPost) userPostService
                        .create(userPost);
                if (userPost != null) {

                    serviceResponse.put("created", userPost);
                    apiResponse.put("apiresponse", serviceResponse);

                    return Response.ok(apiResponse).build();
                }

            } else {
                for (ConstraintViolation<UserPost> error : validateErrors) {
                    apiResponse.put(error.getPropertyPath().toString(),
                            error.getMessage());
                }
                return Response.status(400).entity(apiResponse).build();
            }
        } catch (Exception e) {

            logger.error(e.getMessage());
            apiResponse.put("error", e.getMessage());
        }
        return Response.status(500).entity(apiResponse).build();
    }


    @GET
    @Path("/list/userEmail/{userEmail}")
    @Produces("application/json")
    public Response listByUserEmail(
            @PathParam("userEmail") String userEmail) {

        Map<Object, Object> apiResponse = new HashMap<Object, Object>();
        Map<Object, Object> serviceResponse = new HashMap<Object, Object>();

        try {


            List<UserPost> userPosts = userPostService.listByuserEmail(userEmail
            );
            serviceResponse.put("total", userPosts.size());
            serviceResponse.put("list", userPosts);

            apiResponse.put("apiresponse", serviceResponse);

            return Response.ok(apiResponse).build();

        } catch (Exception e) {

            logger.error("Error in getting UserPost list:", e);
            apiResponse.put("error", e.getMessage());
        }

        return Response.status(500).entity(apiResponse).build();
    }


}
