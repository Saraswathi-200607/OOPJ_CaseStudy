package com.banking.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {
    private PasswordUtil() {}   // Utility class — no instantiation
    
    /**
     * Hashes a plain-text password using SHA-256.
     *
     * @param password plain-text password
     * @return hex-encoded SHA-256 hash
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available.", e);
        }
    }
 
    /**
     * Verifies a plain-text password against a stored hash.
     */
    public static boolean verifyPassword(String plainText, String storedHash) {
        return hashPassword(plainText).equals(storedHash);
    }

}
