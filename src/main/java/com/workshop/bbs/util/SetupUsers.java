package com.workshop.bbs.util;

import com.workshop.bbs.dao.UserDAO;
import com.workshop.bbs.model.User;

import java.util.Optional;

/**
 * Run ONCE after setting up the database to create the default admin users.
 *
 * Usage:
 *   mvn compile exec:java -Dexec.mainClass="com.workshop.bbs.util.SetupUsers"
 *
 * This class:
 *  - Creates admin / admin123
 *  - Creates libuser / lib123
 *  - Creates malinay / admin123
 *
 * It is safe to run multiple times (skips existing users).
 */
public class SetupUsers {

    public static void main(String[] args) {
        System.out.println("=== PUP Book Borrowing System — User Setup ===");
        System.out.println("Workshop · Might Malinay, BSIT 2-2\n");

        String[][] users = {
            {"admin",   "admin123", "Administrator",  "admin"},
            {"libuser", "lib123",   "Maria Santos",   "librarian"},
            {"malinay", "admin123", "Might Malinay",  "admin"},
        };

        UserDAO dao = new UserDAO();
        for (String[] u : users) {
            String username = u[0], password = u[1], fullName = u[2], role = u[3];
            try {
                Optional<User> existing = dao.findByUsername(username);
                if (existing.isPresent()) {
                    System.out.printf("[SKIP] '%s' already exists.%n", username);
                } else {
                    dao.create(username, password, fullName, role);
                    System.out.printf("[OK]   Created user '%s' (%s)%n", username, role);
                }
            } catch (Exception e) {
                System.err.printf("[ERR]  Failed to create '%s': %s%n", username, e.getMessage());
            }
        }

        System.out.println("\nDone. You can now log in with:");
        System.out.println("  admin   / admin123");
        System.out.println("  libuser / lib123");
        System.out.println("  malinay / admin123");
        DatabaseConnection.closePool();
    }
}
