package com.workshop.bbs.util;

/**
 * Run this class ONCE to generate BCrypt hashes for your SQL seed data.
 *
 * Usage:
 *   mvn compile exec:java -Dexec.mainClass="com.workshop.bbs.util.HashGenerator"
 *
 * Copy the printed hashes into sql/init.sql.
 */
public class HashGenerator {

    public static void main(String[] args) {
        String[] passwords = {"admin123", "lib123", "admin123"};
        String[] users     = {"admin",    "libuser", "malinay"};

        System.out.println("=== BCrypt Hashes for sql/init.sql ===");
        for (int i = 0; i < passwords.length; i++) {
            String hash = PasswordUtil.hash(passwords[i]);
            System.out.printf("-- %-10s password: %-10s%n", users[i], passwords[i]);
            System.out.printf("   hash: %s%n%n", hash);
        }
        System.out.println("Paste the hash values into the INSERT INTO users ... statement in sql/init.sql");
    }
}
