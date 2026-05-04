package com.banking.model;

import java.sql.Timestamp;

public class User {
    private int userId;
    private String fullName;
    private String email;
    private String username;
    private String passwordHash;
    private Timestamp createdAt;
    private boolean active;
 
    public User() {}
 
    public User(int userId, String fullName, String email, String username) {
        this.userId   = userId;
        this.fullName = fullName;
        this.email    = email;
        this.username = username;
    }
 
    // ── Getters & Setters ──────────────────────────────────────────
 
    public int getUserId()               { return userId; }
    public void setUserId(int userId)    { this.userId = userId; }
 
    public String getFullName()                  { return fullName; }
    public void setFullName(String fullName)     { this.fullName = fullName; }
 
    public String getEmail()                 { return email; }
    public void setEmail(String email)       { this.email = email; }
 
    public String getUsername()                  { return username; }
    public void setUsername(String username)     { this.username = username; }
 
    public String getPasswordHash()                      { return passwordHash; }
    public void setPasswordHash(String passwordHash)     { this.passwordHash = passwordHash; }
 
    public Timestamp getCreatedAt()                  { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)    { this.createdAt = createdAt; }
 
    public boolean isActive()               { return active; }
    public void setActive(boolean active)   { this.active = active; }
 
    @Override
    public String toString() {
        return "User{userId=" + userId + ", username='" + username + "', fullName='" + fullName + "'}";
    }

}
