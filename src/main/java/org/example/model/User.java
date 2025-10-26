package org.example.model;

public class User {

    public enum UserRole {
        VIEWER, MANAGER, ADMIN
    }

    private final String userId;
    private final String fullName;
    private final String email;
    private final String password;
    private final UserRole role;

    private boolean verified;

    public User(String userId, String fullName, String email, String password, UserRole role) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.verified = false;
    }
    public User(String userId, String fullName, String email, String password, UserRole role, boolean verified) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.verified = verified;
    }

    public String getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }
    public boolean isVerified() {
        return this.verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}