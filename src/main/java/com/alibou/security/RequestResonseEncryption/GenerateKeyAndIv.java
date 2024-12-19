package com.alibou.security.RequestResonseEncryption;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class GenerateKeyAndIv {
    public static void main(String[] args) {
        try {
            // Specific string to seed the SecureRandom
            String specificValue = "AgreeYa!@#$12345";
            byte[] seedValue = specificValue.getBytes();

            // Create a SecureRandom instance seeded with the specific value
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.setSeed(seedValue);

            // Create a KeyGenerator instance for AES
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128, secureRandom); // You can use 128, 192, or 256 for key size

            // Generate the AES key
            SecretKey secretKey = keyGenerator.generateKey();
            String base64Key = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            System.out.println("Generated AES Key (Base64): " + base64Key);

            // Example IV string
            String ivString = "AgreeYa!@#$12345";

            // Generate the IV from the string
            IvParameterSpec iv = generateIvFromString(ivString);

            // Print the IV as a hexadecimal string
            System.out.println("Generated IV: " + bytesToHex(iv.getIV()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public static IvParameterSpec generateIvFromString(String ivString) {
        // Convert the input string to a byte array
        byte[] iv = ivString.getBytes();

        // Ensure the IV is of the required length (16 bytes for AES)
        if (iv.length < 16) {
            // If the input string is shorter than 16 bytes, pad it with zeros
            byte[] paddedIv = new byte[16];
            System.arraycopy(iv, 0, paddedIv, 0, iv.length);
            iv = paddedIv;
        } else if (iv.length > 16) {
            // If the input string is longer than 16 bytes, truncate it
            byte[] truncatedIv = new byte[16];
            System.arraycopy(iv, 0, truncatedIv, 0, 16);
            iv = truncatedIv;
        }

        // Return the IV as an IvParameterSpec object
        return new IvParameterSpec(iv);
    }

    // Helper method to convert byte array to hexadecimal string
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
