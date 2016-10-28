package com.istiak.blooddb;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.istiak.blooddb.utils.AppConstant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Configuration
@ComponentScan
@EnableMongoRepositories(basePackages = {AppConstant.BASE_PACKAGE_NAME})
public class AppConfig extends AbstractMongoConfiguration {
  
  private  final Properties properties = new Properties();
  private final static  Logger logger = LoggerFactory.getLogger(AppConfig.class);
  
  @Override
  public Mongo mongo() throws Exception {
		
	    MongoClientURI uri = new MongoClientURI(getDatabaseURI()+getDatabaseName());
	    MongoClient client = new MongoClient(uri);
        return client;
  }
  
  @Override
  protected  String getDatabaseName() {
	 
		 try {
			 InputStream inputStream = getClass().getClassLoader().getResourceAsStream(AppConstant.PROPERTIES_FILE);
		  	 properties.load(inputStream);

		} catch (IOException e) {
			logger.error("Error:"+e.getMessage());
		}
	  
        return properties.getProperty(AppConstant.PROPERTIES_DB_NAME);
  }
  
  @Override
  protected String getMappingBasePackage() {
      return AppConstant.BASE_PACKAGE_NAME;
  }
  
  protected  String getDatabaseURI() {
	  
	    
	  //logger.info("#############  DB Password after dycryption ##################"+secret);
	  
	  
		 try {
			 InputStream inputStream = getClass().getClassLoader().getResourceAsStream(AppConstant.PROPERTIES_FILE);
		  	 properties.load(inputStream);

		} catch (IOException e) {
			logger.error("Error:"+e.getMessage());
		}
	
		 String dbURI = "mongodb://"+  properties.getProperty(AppConstant.PROPERTIES_DB_USER) + 
				        ":" + properties.getProperty(AppConstant.PROPERTIES_DB_PASSWORD)     +
				         "@" + properties.getProperty(AppConstant.PROPERTIES_DB_IP)      +
				         ":" + properties.getProperty(AppConstant.PROPERTIES_DB_PORT)      + "/";
	  
        logger.info(dbURI);
        
 
		 
		 return dbURI;
  }
} 