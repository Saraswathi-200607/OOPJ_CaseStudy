package com.banking.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // ── Configuration ──────────────────────────────────────────────
    private static final String URL      = "jdbc:mysql://localhost:3306/online_banking";
    private static final String USER     = "root";        // Change to your MySQL username
    private static final String PASSWORD = "Sara@0719";    // Change to your MySQL password
    // ───────────────────────────────────────────────────────────────
 
    private static DatabaseConnection instance;
    private Connection connection;
 
    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection( "jdbc:mysql://localhost:3306/online_banking"
, "root", "Sara@0719");
            System.out.println("[DB] Connection established successfully.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found. Add mysql-connector-j.jar to classpath.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database: " + e.getMessage(), e);
        }
    }
 
    /**
     * Returns the singleton instance, creating it if necessary.
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null || isConnectionClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
 
    /**
     * Returns the active JDBC Connection object.
     */
    public Connection getConnection() {
        return connection;
    }
 
    private static boolean isConnectionClosed() {
        try {
            return instance.connection == null || instance.connection.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }
 
    /**
     * Closes the connection gracefully (call on application exit).
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
