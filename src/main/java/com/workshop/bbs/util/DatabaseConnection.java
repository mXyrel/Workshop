package com.workshop.bbs.util;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection utility.
 * Loads credentials from .env file — never hardcoded.
 */
public class DatabaseConnection {

    private static Connection connection = null;
    private static Dotenv dotenv;

    static {
        try {
            dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();
        } catch (DotenvException e) {
            System.err.println("[WARN] .env file not found. Using system environment variables.");
            dotenv = Dotenv.configure().ignoreIfMissing().load();
        }
    }

    private static String getEnv(String key) {
        String val = dotenv.get(key);
        if (val == null || val.isBlank()) {
            val = System.getenv(key);
        }
        if (val == null || val.isBlank()) {
            throw new RuntimeException("Missing required environment variable: " + key
                    + "\nPlease copy .env.example to .env and fill in your values.");
        }
        return val;
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url  = getEnv("DB_URL");
            String user = getEnv("DB_USER");
            String pass = getEnv("DB_PASSWORD");
            connection = DriverManager.getConnection(url, user, pass);
        }
        return connection;
    }

    public static void closePool() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /** Quick connectivity test used at startup. */
    public static boolean testConnection() {
        try {
            Connection c = getConnection();
            return c != null && !c.isClosed();
        } catch (Exception e) {
            return false;
        }
    }
}
