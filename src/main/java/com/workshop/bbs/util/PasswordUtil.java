package com.workshop.bbs.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility for BCrypt password hashing and verification.
 */
public class PasswordUtil {

    private static final int SALT_ROUNDS = 12;

    /** Hash a plain-text password. */
    public static String hash(String plainText) {
        return BCrypt.hashpw(plainText, BCrypt.gensalt(SALT_ROUNDS));
    }

    /** Verify a plain-text password against a stored hash. */
    public static boolean verify(String plainText, String hashed) {
        try {
            return BCrypt.checkpw(plainText, hashed);
        } catch (Exception e) {
            return false;
        }
    }
}
