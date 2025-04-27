//package com.alibou.security.RequestResonseEncryption;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
//
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.Properties;
//
//public class PropertyUtil {
//    static Logger logger = LoggerFactory.getLogger(EncryptionFilter.class);
//    private static Properties properties;
//
//    static {
//        properties = new Properties();
//        Resource resource = new ClassPathResource("application.properties");
//        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), "UTF-8")) {
//            properties.load(reader);
//        } catch (IOException e) {
//            logger.error("Error loading properties file", e.getStackTrace());
//        }
//    }
//
//    public static String getProperty(String key) {
//        return properties.getProperty(key);
//    }
//}
