package org.example.dao;
import org.example.model.User;
import org.example.model.User.UserRole;
import org.example.util.DBConnection;
import java.sql.*;
import java.util.Optional;

public class userDAOImpl {
    private User currentUser = null;
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        String id = rs.getString("user_id");
        String name = rs.getString("full_name");
        String email = rs.getString("email");
        String pass = rs.getString("password");
        UserRole role = UserRole.valueOf(rs.getString("role"));

        return new User(id, name, email, pass, role);
    }

    public boolean addUser(User user) {
        String sql = "INSERT INTO users (user_id, full_name, email, password, role) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUserId());
            stmt.setString(2, user.getFullName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getRole().name());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            // Log error, especially for duplicate key/constraint violation
            System.err.println("Database error during addUser: " + e.getMessage());
            return false;
        }
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT user_id, full_name, email, password, role FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during getUserByEmail: " + e.getMessage());
        }
        return null;
    }
    public Optional<User> getUserById(String userId) {
        String sql = "SELECT user_id, full_name, email, password, role FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractUserFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during getUserById: " + e.getMessage());
        }
        return Optional.empty();
    }

    public User login(String email, String password) {
        User user = getUserByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            this.currentUser = user;
            return user;
        }
        return null;
    }

    public boolean isAuthorized(UserRole requiredRole) {
        return currentUser != null && currentUser.getRole().ordinal() >= requiredRole.ordinal();
    }

    public void logout() {
        this.currentUser = null;
    }
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET full_name = ?, email = ?, password = ?, role = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole().name());
            stmt.setString(5, user.getUserId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Database error during updateUser: " + e.getMessage());
            return false;
        }
    }
    public boolean deleteUser(String userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Database error during deleteUser: " + e.getMessage());
            return false;
        }
    }
}