package com.istiak.blooddb.controllers;


import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.istiak.blooddb.services.LoginAuditService;
import com.istiak.blooddb.services.UserService;
import com.istiak.blooddb.utils.DistanceUtil;
import com.istiak.blooddb.vo.UserLoginVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import com.istiak.blooddb.entities.User;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



@Path("/users")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	public final int ALLOWED_FAILED_LOGIN_ATTEMPT=5;
	
	@Autowired
	private UserService userService;
	@Autowired
	private LoginAuditService loginAuditService;
    Gson gson = new Gson();



	/******************************* Create **********************************/
	/**
	 * Register user. Fails when email address is duplicated
	 *
	 * @param user
	 *            json object
	 * @return user id & session token + 200 on success / 400 + error message on failure
	 *
	 *
	 * @summary Register user, Fails when email address is duplicated
	 */
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response create(User user) {

		Map<Object, Object> apiResponse = new HashMap<Object, Object>();
		Map<Object, Object> serviceResponse  = new HashMap<Object, Object>();

        String objToJson = gson.toJson(user);
        JsonParser parser = new JsonParser();
        JsonObject userJO = parser.parse(objToJson).getAsJsonObject();
        userJO.remove("password");

		try {
			Set<ConstraintViolation<User>> validateErrors = validator.validate(user);
			//logger.info("creating new user email=" + user.getPhoneNumber());


			if (validateErrors.isEmpty()) {
				
				logger.debug("Calling user service: "+ user.getPhoneNumber());

				user.setPassword(user.getPassword());
				user = (User) userService.create(user);
				
				logger.debug("Done calling UserService");
				
				if(user !=null){

					logger.debug("User done creating user with PhoneNumber :" + user.getPhoneNumber());

					return Response.ok(user).build();

				}

			} else {
				for (ConstraintViolation<User> error : validateErrors) {
					apiResponse.put(error.getPropertyPath().toString(),
							error.getMessage());
				}
				return Response.status(400).entity(apiResponse).build();
			}
		}
		catch (DuplicateKeyException e){
			
			logger.error("Error occured creating user:",e);
            apiResponse.put("message", "Duplicate user found for Phone Number " + user.getPhoneNumber());
			return Response.status(400).entity(apiResponse).build();
        }
		
		catch (Exception e) {

			logger.error("Error occured creating user:",e);
			apiResponse.put("error", e.getMessage());
		}
		return Response.status(500).entity(apiResponse).build();
	}

	/******************************* Retrieve **********************************/
	/**
	 * Get register user
	 *
	 * @param uId
	 *
	 * @return user Object + 200 on success / 500 + error message on failure
	 *
	 *
	 * @summary Get register user
	 */
	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Response get(@PathParam("id") String uId) {

		Map<Object, Object> apiResponse = new HashMap<Object, Object>();
		Map<Object, Object> serviceResponse = new HashMap<Object, Object>();

		try {

			logger.info("Retreiving  user with id=" + uId);
			
			User u = userService.get(uId);
			
			logger.info("User service called with this id=" + uId);
			
			if (u != null) {

				logger.info("User retrieved with =" + uId);
				serviceResponse.put("retreived", u);
				apiResponse.put("apiresponse", serviceResponse);

				return Response.ok(apiResponse).build();

			} else {
				
				logger.info("User not retrived =" + uId);
				serviceResponse.put("success",Boolean.FALSE);
				serviceResponse.put("message","User not found");
				apiResponse.put("response", serviceResponse);
                return  Response.status(404).entity(apiResponse).build();

			}

		} catch (Exception e) {
			logger.error("Error retriving user", e);
			apiResponse.put("error", e.getMessage());
		}

		return Response.status(500).entity(apiResponse).build();
	}
	/**
	 * Get user by email
	 *
	 * @param email
	 *
	 * @return user Object + 200 on success / 400 + error message on failure
	 *
	 *
	 * @summary Get register user
	 */
	@GET
	@Path("/getbyemail/{email}")
	@Produces("application/json")
	public Response getUserByEmail(@PathParam("email") String email) {

		Map<Object, Object> apiResponse = new HashMap<Object, Object>();
		Map<Object, Object> serviceResponse = new HashMap<Object, Object>();
		try {

			logger.info("Retreiving  user with email=" + email);
			User u = userService.getByEmail(email);
			logger.info("User service called with this id=" + email);
			
			if (u != null) {

				serviceResponse.put("retreived", u);
				apiResponse.put("apiresponse", serviceResponse);

				return Response.ok(apiResponse).build();

			} else {

				serviceResponse.put("success",Boolean.FALSE);
				serviceResponse.put("meessage","User not found");
				apiResponse.put("response", serviceResponse);
			}

		} catch (Exception e) {
			logger.error("Error retreiving user with email:", e);
			apiResponse.put("error", e.getMessage());
		}

		return Response.status(500).entity(apiResponse).build();
	}


	/******************************* Update **********************************/
	/**
	 * Update user
	 *
	 * @param user - object
	 *
	 * @return user id & session token + 200 on success / 400 + error message on failure
	 *
	 *
	 * @summary Update user
	 */
	@PUT
	@Path("/{id}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response update(User user) {

		Map<Object, Object> apiResponse = new HashMap<Object, Object>();
		Map<Object, Object> serviceResponse = new HashMap<Object, Object>();
		String objToJson = gson.toJson(user);
		JsonParser parser = new JsonParser();

        JsonObject userJO = parser.parse(objToJson).getAsJsonObject();
        userJO.remove("password");
       // logger.info("Start Updating user  :" + objToJson);

		try {
            User findUser = userService.get(user.getId());
            if (findUser == null) {
                logger.info("user_not_found");
                apiResponse.put("user_not_found","user.primary.email.not.found");
                return Response.status(404).entity(apiResponse).build();
            }
            user.setPassword(findUser.getPassword());
            user.setPasswordsalt(findUser.getPasswordsalt());
            user.setActive(findUser.getActive());

			logger.info("Validating update user with=" + user.getEmail());
			Set<ConstraintViolation<User>> validateErrors = validator
					.validate(user);

			
			if (validateErrors.isEmpty()) {
				logger.info("Calling userservice to update user with=" + user.getEmail());
				user = (User) userService.update(user);

                if (user != null) {
                    objToJson = gson.toJson(user);
                    objToJson.replace("", "password");
                    logger.info("update user info " + objToJson);
                    user.setPasswordsalt(null);
                    user.setPassword(null);
                   // serviceResponse.put(CustomJsonTagName.JSON_TAG_NAME_UPDATE, updatedUser);
                    //apiResponse.put(CustomJsonTagName.JSON_TAG_NAME_API_RESPONSE, serviceResponse);
                    logger.info("Updated user=" + user.getEmail());

                    if(user.getProfileImage()==null)
                    {
                        user.setProfileImage("");
                    }
                    serviceResponse.put("update", user);
                    apiResponse.put("apiresponse", serviceResponse);
                    apiResponse.put("updatedUser", user);

                    return Response.ok(apiResponse).build();

                }

				/*if(user !=null){
					
					logger.info("Updated user=" + user.getEmail());
		
					if(user.getProfileImage()==null)
					{
						user.setProfileImage("");
					}
					serviceResponse.put("update", user);
					apiResponse.put("apiresponse", serviceResponse);
					apiResponse.put("updatedUser", user);

					return Response.ok(apiResponse).build();
				}*/

			} else {
				
				logger.info("Validation error found =" + user.getEmail());
				
				for (ConstraintViolation<User> error : validateErrors) {
					apiResponse.put(error.getPropertyPath().toString(),
							error.getMessage());
				}
				return Response.status(400).entity(apiResponse).build();
			}


		} catch (Exception e) {

			logger.error("", e);
			apiResponse.put("error", e.getMessage());
			logger.info(e.getMessage());

		}
		return Response.status(500).entity(apiResponse).build();
	}

	/******************************* Delete **********************************/
	/**
	 * Delete user
	 *
	 * @param uId - user id
	 *
	 * @return user id & session token + 200 on success / 400 + error message on failure
	 *
	 *
	 * @summary Update user
	 */
	@DELETE
	@Path("/{uId}")
	@Produces("application/json")
	public Response deleteUser(@PathParam("uId") String uId) {

		Map<Object, Object> apiResponse = new HashMap<Object, Object>();
		Map<Object, Object> serviceResponse  = new HashMap<Object, Object>();

		try {

			logger.info("Deleting user=" + uId);

			boolean b = userService.delete(uId);
			serviceResponse.put("deleted", b);
			apiResponse.put("apiresponse", serviceResponse);

			return Response.ok(apiResponse).build();
		} catch (Exception e) {

			logger.error("Error", e);
			apiResponse.put("error", e.getMessage());
		}

		return Response.status(500).entity(apiResponse).build();
	}

	/******************************* list **********************************/
	/**
	 * @return JSON list
	 * @summary Get user list
	 */
	@GET
    @Produces("application/json")
    public Response list() {

		Map<Object, Object> apiResponse     = new HashMap<Object, Object>();
		Map<Object, Object> serviceResponse = new HashMap<Object, Object>();

		try {

			logger.debug("Getting users list by limit=");

			List<User> users = userService.list();


			serviceResponse.put("total", users.size());
			serviceResponse.put("list", users);

			apiResponse.put("apiresponse", serviceResponse);

			return Response.ok(apiResponse).build();

		} catch (Exception e) {

			logger.error("Error in getting user list:", e);
			apiResponse.put("error", e.getMessage());
		}

		return Response.status(500).entity(apiResponse).build();
	}

	/******************************* Others **********************************/
	/**
	 * Login user. Fails when email address or password does not match with
	 * existing record
	 *
	 * @param userLogin
	 *            json object
	 * @return token
	 *
	 *         <pre>
	 * {"token":"00366B85-16A3-4F89-B8FA-860237C07AD11416754983425"}
	 * </pre>
	 * @summary Login user. Fails when email address or password does not match
	 *          with existing record
	 */
	@POST
	@Path("/login")
	@Consumes("application/json")
	@Produces("application/json")
	public Response userLogin(@Context HttpServletRequest requestContext ,UserLoginVO userLogin) {

		Map<Object, Object> apiResponse = new HashMap<Object, Object>();
		Map<Object, Object> serviceResponse = new HashMap<Object, Object>();
		Map<Object, Object> loginResponse = new HashMap<Object, Object>();
		
		
		
		try {
			
			logger.info("Validating user login with=" + userLogin.getEmail());
				
			Set<ConstraintViolation<UserLoginVO>> validateErrors = validator
					.validate(userLogin);
			
			User user=new User();
			
			if (validateErrors.isEmpty()) {
			
				logger.info("Validating update user with=" + user.getEmail());
				//
				int  numberOfFailureLogin = loginAuditService.numberOfLogin(userLogin.getEmail());
				//locked account if  continuous five failure login attempt
				
				logger.info("Chekcing number of failed login =" + user.getEmail());
				
				if(numberOfFailureLogin==ALLOWED_FAILED_LOGIN_ATTEMPT)
				{
					logger.info("Too many failed login =" + numberOfFailureLogin);
					
					user.setActive("0");
					String  unlockTime = loginAuditService.lastLogin(userLogin.getEmail());
					serviceResponse.put("message", "Your account is locked");
					serviceResponse.put("unlocktime", "Please try after "+unlockTime);
					apiResponse.put("apiresponse", serviceResponse);
					return Response.status(200).entity(apiResponse).build();
				}
				
				logger.info("Trying to login user =" + user.getEmail());
				
				user = userService.login(requestContext,userLogin);
				if (user == null) {
					
					logger.info("No user returned ");
					serviceResponse.put("message", "User Name or Password Mismatch");
					apiResponse.put("apiresponse", serviceResponse);
					return Response.status(200).entity(apiResponse).build();

				} else if (Integer.parseInt(user.getActive()) == 0) {
					
					logger.info("Inactive user ");

					serviceResponse.put("message", "User is inactive.");
					apiResponse.put("apiresponse", serviceResponse);

					return Response.status(200).entity(apiResponse).build();

				} else {

					logger.info("User logged in ");
					
					loginResponse.put("loggedin", Boolean.TRUE);
					loginResponse.put("SessionToken", user.getSessionToken());
					loginResponse.put("id",user.getId());
					loginResponse.put("name", user.getName());
					serviceResponse.put("login", loginResponse);
					apiResponse.put("apiresponse", serviceResponse);

					return Response.status(200).entity(apiResponse).build();

				}
			} else {
				
				logger.info("Validation errors ");
				for (ConstraintViolation<UserLoginVO> error : validateErrors) {

					apiResponse.put(error.getPropertyPath().toString(),
							error.getMessage());
				}
				return Response.status(400).entity(apiResponse).build();
			}


		} catch (Exception e) {
			logger.error("Error occured while logging in user:", e);

			apiResponse.put("error", e.getMessage());
		}
		return Response.status(500).entity(apiResponse).build();
	}

	/**
	 * Logout user. token need to pass with header
	 *
	 *
	 * @return 200 on logout successfully / 400 when failed to logout
	 * @summary Logout user. token need to pass with header
	 */

	@GET
	@Path("/logout/{uId}")
	@Produces({ "application/json" })
	public Response userLogout( @PathParam("uId") String uId) {

		Map<Object, Object> apiResponse = new HashMap<Object, Object>();
		Map<Object, Object> serviceResponse = new HashMap<Object, Object>();


		try {
			if (uId == null || uId.equals("")) {

				serviceResponse.put("messeage", "uId missing");
				apiResponse.put("apiresponse", serviceResponse);
				return Response.status(400).entity(apiResponse).build();

			}
			else if (!userService.logout(uId)) {

				serviceResponse.put("message", "Invalid auth uId");
				apiResponse.put("apiresponse", serviceResponse);

				return Response.status(400).build();

			}else {

				serviceResponse.put("loggedout", Boolean.TRUE);
				apiResponse.put("response", serviceResponse);
				return Response.status(200).entity(apiResponse).build();

			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			apiResponse.put("error", e.getMessage());
		}
		return Response.status(500).entity(apiResponse).build();
	}

	

	/*@GET
	@Path("/search/{searchTerm}")
	@Produces("application/json")
	public Response search(@PathParam("searchTerm") String searchTerm) {
		Map<Object, Object> apiResponse = new HashMap<Object, Object>();
		Map<Object, Object> jsonResponse = new HashMap<Object, Object>();

		List<User> users = userRepository.findByFirstnameRegex(searchTerm);
		jsonResponse.put("total", users.size());
		jsonResponse.put("list", users);

		apiResponse.put("apiresponse", jsonResponse);

		return Response.ok(apiResponse).build();
	}*/

	@GET
	@Path("/search/{searchTerm}/{startDate}/{endDate}")
	@Produces("application/json")
	public Response searchByDate(@PathParam("searchTerm") String searchTerm,
								 @PathParam("startDate") String startDate,
								 @PathParam("endDate") String endDate) {

		Map<Object, Object> apiResponse = new HashMap<Object, Object>();
		Map<Object, Object> jsonResponse = new HashMap<Object, Object>();

		try{

			List<User> users = userService.findUserByDate(searchTerm, startDate, endDate);
			jsonResponse.put("total", users.size());
			jsonResponse.put("list", users);

			apiResponse.put("apiresponse", jsonResponse);

			return Response.status(200).entity(apiResponse).build();

		} catch (Exception e) {
			logger.error(e.getMessage());
			apiResponse.put("error", e.getMessage());
		}

		return Response.status(500).entity(apiResponse).build();
	}
	@GET
	@Path("/list/birthdaymonth/{month}")
	@Produces("application/json")
	public Response findByBDate(@PathParam("month") Integer month) {
		Map<Object, Object> apiResponse = new HashMap<Object, Object>();
		Map<Object, Object> jsonResponse = new HashMap<Object, Object>();
		try{
			List<User> users = userService.findByBDate(month);
			jsonResponse.put("total", users.size());
			jsonResponse.put("list", users);

			apiResponse.put("apiresponse", jsonResponse);

			return Response.status(200)
					.entity(apiResponse)
					.build();
		} catch (Exception e) {
			logger.error(e.getMessage());
			apiResponse.put("error", e.getMessage());
		}

		return Response.status(500).entity(apiResponse).build();
	}



	@GET
	@Path("/search")
	@Produces("application/json")
	public Response searchByBloodGroup(
            @QueryParam("userLatitude") String userLatitude,
            @QueryParam("userLongitude") String userLongitude,
            @QueryParam("bloodGroup") String bloodGroup) {
        logger.debug(">> searchByBloodGroup({},{},{},{})", userLatitude, userLongitude, bloodGroup);

		Map<Object, Object> apiResponse = new HashMap<Object, Object>();
		Map<Object, Object> response = new HashMap<Object, Object>();

		try {
			logger.info("Searching users.");
            logger.debug(">> userService.searchByBloodGroup({},{},{},{})", userLatitude, userLongitude, bloodGroup);
            List<User> users = userService.getByLocationAndBloodGroup(bloodGroup);

            if (users != null) {
                for (User user : users) {
                    DistanceUtil distanceUtil = new DistanceUtil();
                    String distance = distanceUtil.distance(Double.parseDouble(userLatitude), Double.parseDouble(userLongitude), Double.parseDouble(user.getUserLatitude()), Double.parseDouble(user.getUserLongitude()));
                    user.setDistanceFromCurrentUser(distance);
                }
            }

            Collections.sort(users);

            logger.debug("<< userService.getbylocation({},{},{},{}) < returning: {}", userLatitude, userLongitude, bloodGroup);

            response.put("response", users);
            response.put("total", users.size());
            apiResponse.put("apiresponse", response);
            return Response.ok(apiResponse).build();
		} catch (Exception e) {
            apiResponse.put("error", e.getMessage());
		}

		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
	}


	

}
