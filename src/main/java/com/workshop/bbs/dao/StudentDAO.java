package com.workshop.bbs.dao;

import com.workshop.bbs.model.Student;
import com.workshop.bbs.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDAO {

    private Student map(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("id"),
                rs.getString("student_number"),
                rs.getString("full_name"),
                rs.getString("course"),
                rs.getString("year_level"),
                rs.getString("email")
        );
    }

    public List<Student> findAll() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY full_name";
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Optional<Student> findById(int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(map(rs));
        }
        return Optional.empty();
    }

    public void create(Student s) throws SQLException {
        String sql = "INSERT INTO students (student_number, full_name, course, year_level, email) VALUES (?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getStudentNumber());
            ps.setString(2, s.getFullName());
            ps.setString(3, s.getCourse());
            ps.setString(4, s.getYearLevel());
            ps.setString(5, s.getEmail());
            ps.executeUpdate();
        }
    }

    public void update(Student s) throws SQLException {
        String sql = "UPDATE students SET student_number=?, full_name=?, course=?, year_level=?, email=? WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getStudentNumber());
            ps.setString(2, s.getFullName());
            ps.setString(3, s.getCourse());
            ps.setString(4, s.getYearLevel());
            ps.setString(5, s.getEmail());
            ps.setInt(6, s.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public int countAll() throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM students")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
