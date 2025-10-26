package org.example.dao;

import org.example.model.User;
import org.example.model.User.UserRole;
import org.example.util.DBConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOImplTest {

    private userDAOImpl userDAO;
    private final String TEST_ID = "uid123";
    private final String TEST_EMAIL = "testuser@example.com";
    private final String TEST_PASS = "securepassword123";
    private final String TEST_NAME = "Test User";
    private final User TEST_USER = new User(
            TEST_ID, TEST_NAME, TEST_EMAIL, TEST_PASS, UserRole.VIEWER
    );
    private final String ADMIN_EMAIL = "admin@inventory.com";
    private final String ADMIN_PASS = "admin123";

    @BeforeEach
    public void setup() {
        cleanTable();
        userDAO = new userDAOImpl();
    }

    private void cleanTable() {
        String sql = "DELETE FROM users";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("✅ Users table cleaned successfully.");
        } catch (SQLException e) {
            System.err.println("❌ Setup/Cleanup Error: Could not clean users table. Check DBConnection settings.");
            fail("Database connection failed during setup/cleanup: " + e.getMessage());
        }
    }

    @Test
    void testAddUser_DuplicateEmailFailure() {
        userDAO.addUser(TEST_USER);
        User duplicateUser = new User("uid2", "Another Name", TEST_EMAIL, "anotherpass", UserRole.MANAGER);

        boolean result = userDAO.addUser(duplicateUser);
        assertFalse(result, "Adding a user with a duplicate email should return false (due to DB constraint).");
    }


    @Test
    void testLogin_Failure_IncorrectPassword() {
        userDAO.addUser(TEST_USER);
        User loggedInUser = userDAO.login(TEST_EMAIL, "wrongpassword");
        assertNull(loggedInUser, "Login should fail with an incorrect password.");
    }

    @Test
    void testLogin_Failure_UserNotFound() {
        User loggedInUser = userDAO.login("nonexistent@email.com", TEST_PASS);
        assertNull(loggedInUser, "Login should fail if the user email does not exist.");
    }

    @Test
    void testUpdateUser_Failure_NonExistentUser() {
        User nonExistentUser = new User(
                "nonexistentID", "Ghost User", "ghost@example.com", "pass", UserRole.VIEWER
        );

        boolean result = userDAO.updateUser(nonExistentUser);

        assertFalse(result, "Updating a user with a non-existent ID should return false.");
    }


    @Test
    void testDeleteUser_Failure_NonExistentUser() {
        boolean deleteResult = userDAO.deleteUser("nonexistent-delete-id");
        assertFalse(deleteResult, "Deleting a non-existent user should return false.");
    }

    @Test
    void addUser() {
    }

    @Test
    void getUserById() {
    }

    @Test
    void getUserByEmail() {
    }

    @Test
    void getAllUsers() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void login() {
    }

    @Test
    void isAuthorized() {
    }

    @Test
    void getCurrentUser() {
    }

    @Test
    void logout() {
    }

    @Test
    void updateVerificationToken() {
    }
}