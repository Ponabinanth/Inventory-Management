package org.example.model;

public class User {
    public enum UserRole { VIEWER, MANAGER }

    private final String userId;
    private final String fullName;
    private final String email;
    private final String password; // Simplified for demo
    private final UserRole role;

    public User(String userId, String fullName, String email, String password, UserRole role) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public UserRole getRole() { return role; }
}