package com.alibou.security.RequestResonseEncryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class InternalEncryptionDecryption {
    static Logger logger = LoggerFactory.getLogger(InternalEncryptionDecryption.class);
    public static String key;
    public static String iv;

    static final String cipherMode = "AES/CBC/PKCS5Padding";
    static final String charsetName = "UTF-8";

    static {
        // Load properties using PropertyUtil
        key = PropertyUtil.getProperty("encryption.key");
        iv = PropertyUtil.getProperty("encryption.iv");

        // Debug logs to check if properties are loaded correctly
        if (key == null || iv == null) {
            logger.error("Key or IV is null. Ensure properties are defined in application.properties.");
        }
    }

    public static String encryptCBC(String message) throws Exception {
        if (message == null || message.trim().isEmpty()) {
            return message;
        }

        logger.info("encryptCBC method started");
        logger.info("Decrypted String : {} " , message);

        byte[] content = message.getBytes(charsetName);
        byte[] keyByte = key.getBytes(charsetName);
        SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
        byte[] ivByte = convertIvStringToByteArray(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(ivByte);

        Cipher cipher = Cipher.getInstance(cipherMode);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] data = cipher.doFinal(content);
        String result = Base64.getEncoder().encodeToString(data);

        logger.info("Encrypted String : {} " , result);
        logger.info("encryptCBC method finished");
        return result;
    }

    public static String decryptCBC(String message) throws Exception {
        logger.info("decryptCBC method started");
        logger.info("Encrypted : {} " , message);

        if (message == null || message.trim().isEmpty()) {
            return message;
        }

        byte[] messageByte = Base64.getDecoder().decode(message);
        byte[] keyByte = key.getBytes(charsetName);
        SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
        byte[] ivByte = convertIvStringToByteArray(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(ivByte);

        Cipher cipher = Cipher.getInstance(cipherMode);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] content = cipher.doFinal(messageByte);
        String result = new String(content, charsetName);

        logger.info("Decrypted : {} " , result);
        logger.info("decryptCBC method finished");
        return result;
    }

    // Helper method to convert the IV string to a byte array
    private static byte[] convertIvStringToByteArray(String ivString) {
        // Check if the input string is of the correct length (32 hexadecimal characters)
        if (ivString.length() != 32) {
            throw new IllegalArgumentException("IV string must be 32 hexadecimal characters long.");
        }

        // Convert the hexadecimal string to a byte array
        byte[] iv = new byte[16];
        for (int i = 0; i < 16; i++) {
            int index = i * 2;
            iv[i] = (byte) Integer.parseInt(ivString.substring(index, index + 2), 16);
        }

        return iv;
    }

    //TO DECRYPT AND ENCRYPT API RESPONSE AND REQUEST
    public static void main(String[] args) {

        try {
            // Sample data to encrypt
            String data = "{\n" +
                    "  \"email\": \"admin@mail.com\",\n" +
                    "  \"password\": \"password\"\n" +
                    "}";
            // decrypt the data
            String decrypt = "uLJkeTWGIkjZyZTlHUpoyN/wd6J7Pkgfg61C7sSgS56H4H1wbnS42r1TEkhQEKKWKxwao1EXSIl1o0eygemuk1wTsYsbSVh1964KpUuwoA2248CTYxyxdCVdBHl7rBlJ2WnZ0JNFvV3zFYyJy89OAGgHDqGsSF3Z2VopV8CfebA=";
            String encryptedData = encryptCBC(data);
            logger.info("Encrypted Data: {} " , encryptedData);

            // Decrypt the data
            String decryptedData = decryptCBC(encryptedData);
            logger.info("Decrypted Data: {} " , decryptedData);
        } catch (Exception e) {
            logger.error("something went wrong in main method {} ", e.getStackTrace());
        }
    }
}