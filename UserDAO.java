package com.banking.dao;
import com.banking.model.User;
import com.banking.util.DatabaseConnection;
import com.banking.util.PasswordUtil;
 
import java.sql.*;

public class UserDAO {
    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }
 
    /**
     * Registers a new user in the database.
     *
     * @return generated user_id, or -1 on failure
     */
    public synchronized int registerUser(String fullName, String email,
                                         String username, String password) throws SQLException {
        String sql = "INSERT INTO users (full_name, email, username, password_hash) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, username);
            ps.setString(4, PasswordUtil.hashPassword(password));
            ps.executeUpdate();
 
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }
 
    /**
     * Authenticates a user by username and password.
     *
     * @return User object on success, null on failure
     */
    public synchronized User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND is_active = TRUE";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (PasswordUtil.verifyPassword(password, storedHash)) {
                        return mapUser(rs);
                    }
                }
            }
        }
        return null;
    }
 
    /**
     * Checks if a username already exists.
     */
    public synchronized boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
 
    /**
     * Checks if an email already exists.
     */
    public synchronized boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
 
    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        u.setActive(rs.getBoolean("is_active"));
        return u;
    }

}
