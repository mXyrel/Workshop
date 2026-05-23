package com.workshop.bbs.dao;

import com.workshop.bbs.model.BorrowRecord;
import com.workshop.bbs.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BorrowRecordDAO {

    private BorrowRecord map(ResultSet rs) throws SQLException {
        BorrowRecord r = new BorrowRecord();
        r.setId(rs.getInt("id"));
        r.setStudentId(rs.getInt("student_id"));
        r.setBookId(rs.getInt("book_id"));
        Date borrowDate = rs.getDate("borrow_date");
        if (borrowDate != null) r.setBorrowDate(borrowDate.toLocalDate());
        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) r.setDueDate(dueDate.toLocalDate());
        Date returnDate = rs.getDate("return_date");
        if (returnDate != null) r.setReturnDate(returnDate.toLocalDate());
        r.setStatus(rs.getString("status"));
        // Joined fields (may be null if not joined)
        try { r.setStudentName(rs.getString("student_name")); } catch (SQLException ignored) {}
        try { r.setStudentNumber(rs.getString("student_number")); } catch (SQLException ignored) {}
        try { r.setBookTitle(rs.getString("book_title")); } catch (SQLException ignored) {}
        return r;
    }

    public List<BorrowRecord> findAll() throws SQLException {
        List<BorrowRecord> list = new ArrayList<>();
        String sql = """
            SELECT br.*, s.full_name AS student_name, s.student_number, b.title AS book_title
            FROM borrow_records br
            JOIN students s ON br.student_id = s.id
            JOIN books b ON br.book_id = b.id
            ORDER BY br.borrow_date DESC
            """;
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<BorrowRecord> findByStatus(String status) throws SQLException {
        List<BorrowRecord> list = new ArrayList<>();
        String sql = """
            SELECT br.*, s.full_name AS student_name, s.student_number, b.title AS book_title
            FROM borrow_records br
            JOIN students s ON br.student_id = s.id
            JOIN books b ON br.book_id = b.id
            WHERE br.status = ?
            ORDER BY br.borrow_date DESC
            """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Optional<BorrowRecord> findById(int id) throws SQLException {
        String sql = """
            SELECT br.*, s.full_name AS student_name, s.student_number, b.title AS book_title
            FROM borrow_records br
            JOIN students s ON br.student_id = s.id
            JOIN books b ON br.book_id = b.id
            WHERE br.id = ?
            """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(map(rs));
        }
        return Optional.empty();
    }

    /** Create a new borrow record and decrement book availability. */
    public void create(BorrowRecord r) throws SQLException {
        String sql = "INSERT INTO borrow_records (student_id, book_id, borrow_date, due_date, status) VALUES (?,?,?,?,'borrowed')";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, r.getStudentId());
            ps.setInt(2, r.getBookId());
            ps.setDate(3, Date.valueOf(r.getBorrowDate() != null ? r.getBorrowDate() : LocalDate.now()));
            ps.setDate(4, Date.valueOf(r.getDueDate() != null ? r.getDueDate() : LocalDate.now().plusDays(7)));
            ps.executeUpdate();
        }
        new BookDAO().decrementAvailable(r.getBookId());
    }

    /** Mark a record as returned and increment book availability. */
    public void markReturned(int recordId) throws SQLException {
        // Fetch book_id first
        Optional<BorrowRecord> opt = findById(recordId);
        if (opt.isEmpty()) return;
        BorrowRecord r = opt.get();

        String sql = "UPDATE borrow_records SET status='returned', return_date=? WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setInt(2, recordId);
            ps.executeUpdate();
        }
        new BookDAO().incrementAvailable(r.getBookId());
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM borrow_records WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public int countByStatus(String status) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM borrow_records WHERE status = ?")) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /** Bulk-update overdue records (status = 'borrowed' and due_date < today). */
    public void refreshOverdueStatuses() throws SQLException {
        String sql = "UPDATE borrow_records SET status='overdue' WHERE status='borrowed' AND due_date < CURRENT_DATE";
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement()) {
            st.executeUpdate(sql);
        }
    }
}
