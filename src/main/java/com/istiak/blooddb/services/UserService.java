package com.istiak.blooddb.services;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import org.bson.types.ObjectId;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.istiak.blooddb.dao.SpringDataDBUtils;
import com.istiak.blooddb.entities.User;
import com.istiak.blooddb.utils.AppConstant;
import com.istiak.blooddb.vo.UserLoginVO;
import com.mongodb.DBObject;


@Service
@Repository
//@Secured({ "ROLE_USER", "ROLE_ADMIN" })
public class UserService {

	@Autowired
	private LoginAuditService loginAuditService;

    private static final int KEY_ITERATION_COUNT = 128;
    private static final int KEY_LENGTH = 256;
    private Cipher ecipher;
    private Cipher dcipher;

    private Key generateKeyValue(String salt) throws Exception {
        // TO DO
        String passPhrase = "hello";

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(passPhrase.toCharArray(), salt.getBytes("UTF-8"), KEY_ITERATION_COUNT, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        byte[] iv = new byte[16];
        AlgorithmParameterSpec spec2 = new IvParameterSpec(iv);

        ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        ecipher.init(Cipher.ENCRYPT_MODE, secret, spec2);

        dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        dcipher.init(Cipher.DECRYPT_MODE, secret, spec2);

        return secret;
    }

    public String encrypt(String encrypt) throws Exception {

        Base64 encoder = new Base64();

        byte[] bytes = encrypt.getBytes("UTF-8");
        byte[] encrypted = encrypt(bytes);

        return  encoder.encodeAsString(encrypted);
    }

   
     public byte[] encrypt(byte[] plain) throws Exception {

        return ecipher.doFinal(plain);
    }

 
     public String decrypt(String encrypt) throws Exception {

        Base64 decoder = new Base64();

        byte[] bytes = decoder.decode( encrypt);
        byte[] decrypted = decrypt(bytes);

        return new String(decrypted, "UTF-8");
    }


    public byte[] decrypt(byte[] encrypt) throws Exception {
        return dcipher.doFinal(encrypt);
    }

	public Object create(User user) throws Exception {

		String salt = this.generateSalt();// this.getSalt();
		Long time = new Date().getTime();
		String token = UUID.randomUUID().toString().toUpperCase()
				+ time.toString();

        this.generateKeyValue(salt);

	    user.setActive("1");
		user.setPasswordsalt(salt);
//		user.setPassword(this.getSecurePassword(user.getPassword(), salt));
        System.out.println(user.getPhoneNumber());
        user.setPassword(this.encrypt(user.getPhoneNumber()));
        user.setSessionToken(token);
        user.setSessionExpireTime( LocalDateTime.now().plusDays(1) );
		user.setJoiningDate(LocalDateTime.now());


		SpringDataDBUtils.getMongoOperations().insert(user);
		
		return user;
	}

	public User get(String userId) throws Exception {

		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(userId));

		return SpringDataDBUtils.getMongoOperations().findOne(query, User.class);
	}

	public User getByEmail(String email) throws Exception {

		Query query = new Query();
		query.addCriteria(Criteria.where("email").is(email));


		return SpringDataDBUtils.getMongoOperations().findOne(query, User.class);

	}
	
	public User getBySessionToken(String token) throws Exception {

		Query query = new Query();
		query.addCriteria(Criteria.where("sessionToken").is(token));
		//query.fields().exclude("password");
		//query.fields().exclude("passwordsalt");

		return SpringDataDBUtils.getMongoOperations().findOne(query, User.class);

	}
	
	public User update(User user) throws Exception {

		user.setDateModified(LocalDateTime.now());
		SpringDataDBUtils.getMongoOperations().save(user);
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(new ObjectId(user.getId())));
		User updatedUserInfo = SpringDataDBUtils.getMongoOperations().findOne(query, User.class);
		return updatedUserInfo;
	}

	public User updateProPic(User user) throws Exception {
		
		user.setDateModified(LocalDateTime.now());
		SpringDataDBUtils.getMongoOperations().save(user);
		
		return user;
	}
	
	
	public User updateWithoutDecrypt(User user) throws Exception {
	//	user.setPassword(this.encrypt(this.decrypt(user.getPassword())));
		user.setDateModified(LocalDateTime.now());
		SpringDataDBUtils.getMongoOperations().save(user);
		
		return user;
	}
	
	public boolean delete(String userId) throws Exception {

		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(userId));
		User u =  SpringDataDBUtils.getMongoOperations().findAndRemove(query, User.class);
		 
		return (u==null?false:true);
	}


	public User login(HttpServletRequest requestContext,UserLoginVO userLogin) throws Exception {
		User user = getByEmail(userLogin.getEmail());

		if (user == null)
		{
			loginAuditService.loginAudit(requestContext, userLogin, "Account does not exists", false);
			//loginAuditing.seti
			return null;
		}

		String salt = user.getPasswordsalt();
//        String generatedPassword = this.getSecurePassword(userLogin.getPassword(), salt);
		this.generateKeyValue(salt);

		if (Integer.parseInt(user.getActive()) == 0) {
			loginAuditService.loginAudit(requestContext, userLogin, "Inactive user", true);
			return user;
		} else if (userLogin.getPassword().equals(this.decrypt(user.getPassword())) && Integer.parseInt(user.getActive()) == 1) {

			String userId = user.getId();
			String token = "";
			if (user.getSessionToken() == null || user.getSessionToken().equals("")) {
				token = UUID.randomUUID().toString()
						+ userId + System.currentTimeMillis();
				user.setSessionToken(token);


				user.setLastLoginTime( LocalDateTime.now());
				update(user);
			} else {
				user.setSessionExpireTime( LocalDateTime.now().plusDays(1) );
				user.setLastLoginTime(LocalDateTime.now());

				update(user);
			}
			// return token

		} else{
			loginAuditService.loginAudit(requestContext, userLogin, "Password  mismatch", false);
			return null;
		}
		loginAuditService.loginAudit(requestContext, userLogin, "Login success", true);
		return user;
	}
	
	public List<User> list() throws Exception {

		List<User> users = new ArrayList<User>();
		Query query = new Query();
		
		query.fields().exclude("password");
		query.fields().exclude("passwordsalt");
		
		users = SpringDataDBUtils.getMongoOperations().find(query, User.class);

		return users;
	}

	public List<User> findUserByDate(String searchTerm, String startDate, String endDate) throws Exception {
		List<User> users = new ArrayList<User>();
		Query query = new Query(
				Criteria.where(searchTerm)
						.gte(LocalDate.parse(startDate))
						.lte(LocalDate.parse(endDate))
		);
		query.fields().exclude("password");
		query.fields().exclude("passwordsalt");

		users = SpringDataDBUtils.getMongoOperations().find(query, User.class);

		return users;
	}
	
	
	public List<User> findByBDate(Integer month) throws Exception {
		List<User> users = new ArrayList<User>();

		TypedAggregation<User> aggMonth = newAggregation(User.class, project() //
				.andExpression("month(birthDate)").as("month"),match(where("month").is(month)) //
		);

		AggregationResults<DBObject> resultsMonth = SpringDataDBUtils.getMongoOperations().aggregate(aggMonth, DBObject.class);

		List<DBObject> list = resultsMonth.getMappedResults();

		if(list.size() >0){
			for (int i=0; i < list.size(); i++){
				String userId = list.get(i).get("_id").toString();
				Query query = new Query();
				query.addCriteria(Criteria.where("id").is(userId));
				query.fields().exclude("password");
				query.fields().exclude("passwordsalt");
				users.add(SpringDataDBUtils.getMongoOperations().findOne(query, User.class));
			}
		}

		return users;
	}


	public List<User> getByNameOrEmail(String firstName,String lastName,String email)
			throws Exception {
		if(firstName!=null||lastName!=null||email!=null) {
			List<User> users = new ArrayList<User>();
			Query query = new Query();
			//query.addCriteria(Criteria.where("firstName").is(firstName).orOperator(Criteria.where("lastname").is(lastName)).orOperator(Criteria.where("email").is(email)));
			if (firstName != null) {
				query.addCriteria(Criteria.where("firstname").regex(Pattern.compile(firstName, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)));
			}
			if (lastName != null) {
				query.addCriteria(Criteria.where("lastname").regex(Pattern.compile(lastName, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)));
			}
			if (email != null) {
				query.addCriteria(Criteria.where("email").regex(Pattern.compile(email, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)));
			}

			users = SpringDataDBUtils.getMongoOperations().find(query, User.class);
			if (email != null&&users.size()==0) {
				query = new Query();
				query.addCriteria(Criteria.where("emailList.value").is(email));
				users = SpringDataDBUtils.getMongoOperations().find(query, User.class);
			}
			return users;
		}else
		{
			return null;
		}
	}

    public List<User> getByLocationAndBloodGroup(String bloodGroup) throws Exception {
        if (bloodGroup != null) {
            List<User> users = new ArrayList<User>();
			Query query = new Query(
                    Criteria.where("bloodGroup").is(bloodGroup));

			users = SpringDataDBUtils.getMongoOperations().find(query, User.class);


            return users;
        }else
        {
            return null;
        }
    }


	public Boolean logout(String uId) throws Exception { 
		
		User user = get(uId);
		if (user != null) {
	       
			user.setSessionToken("");
			user.setSessionExpireTime( LocalDateTime.now().minusDays(1) );
		
	        
	    	SpringDataDBUtils.getMongoOperations().save(user);
	        return true;
		}

		return false;
	}
	
	public Boolean isLogin(User userModel) {
		return false;
	}
    public String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        String s = new String(bytes);
        return s;
    }
	public String getSalt() throws NoSuchAlgorithmException,
			NoSuchProviderException {
		
		String uuid = UUID.randomUUID().toString().toUpperCase();
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] bytes = md.digest(uuid.getBytes());
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16)
					.substring(1));
		}
		// Get complete hashed password in hex format
		return sb.toString();
	}

	public String getSecurePassword(String passwordToHash, String salt) {
		
		String generatedPassword = "";
		try {
			// Create MessageDigest instance for MD5
			MessageDigest md = MessageDigest.getInstance("MD5");
			// Add password bytes to digest
			md.update(salt.getBytes());
			// Get the hash's bytes
			byte[] bytes = md.digest(passwordToHash.getBytes());
			// This bytes[] has bytes in decimal format;
			// Convert it to hexadecimal format
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
			// Get complete hashed password in hex format
			generatedPassword = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return generatedPassword;
	}

	public String toCamelCase(String s) {
		String[] parts = s.split(" ");
		String camelCaseString = "";
		for (String part : parts) {
			camelCaseString = camelCaseString + toProperCase(part);
		}
		return camelCaseString.trim();
	}


	static String toProperCase(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase()
				+ " ";
	}
}
