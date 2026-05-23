package com.workshop.bbs.dao;

import com.workshop.bbs.model.User;
import com.workshop.bbs.util.DatabaseConnection;
import com.workshop.bbs.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {

    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password_hash, full_name, role FROM users WHERE username = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setFullName(rs.getString("full_name"));
                u.setRole(rs.getString("role"));
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }

    /** Authenticate: return the User if credentials match, empty otherwise. */
    public Optional<User> authenticate(String username, String password) throws SQLException {
        Optional<User> userOpt = findByUsername(username);
        if (userOpt.isPresent()) {
            User u = userOpt.get();
            if (PasswordUtil.verify(password, u.getPasswordHash())) {
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }

    public List<User> findAll() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, username, full_name, role FROM users ORDER BY full_name";
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new User(rs.getInt("id"), rs.getString("username"),
                        rs.getString("full_name"), rs.getString("role")));
            }
        }
        return list;
    }

    public void create(String username, String plainPassword, String fullName, String role) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, full_name, role) VALUES (?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, PasswordUtil.hash(plainPassword));
            ps.setString(3, fullName);
            ps.setString(4, role);
            ps.executeUpdate();
        }
    }
}
