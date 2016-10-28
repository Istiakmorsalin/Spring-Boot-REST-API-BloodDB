/**
 * Imtiaz Mirza imz.mrz@gmail.com 
 */
package com.istiak.blooddb.utils;


import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BDUtils {

    private static final Properties properties = new Properties();
    private final static Logger logger = LoggerFactory.getLogger(BDUtils.class);

    public static String getProperty(String propertyStr) {

        try {
            InputStream inputStream = BDUtils.class.getClassLoader().getResourceAsStream(AppConstant.PROPERTIES_FILE);
            properties.load(inputStream);

        } catch (IOException e) {
            logger.error("Error:" + e.getMessage());
        }

        return properties.getProperty(propertyStr);
    }

    /*
    Save input file stream to new location
    */
    public Map<Object, Object> saveToFileInBytes(byte[] uploadedBytes,
                                                  String uploadedFileLocation) {
        Map<Object, Object> response = new HashMap<Object, Object>();
        try {
            OutputStream out = null;

            out = new FileOutputStream(new File(uploadedFileLocation));

            out.write(uploadedBytes);
            out.flush();
            out.close();
            response.put("success", "true");
            response.put("message", "File uploaded successfully");
            return response;
        } catch (IOException e) {
            response.put("success", "false");
            response.put("message", "File upload failed");
            return response;
        }
    }

}
