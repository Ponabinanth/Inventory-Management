package org.example.dao;
import org.example.model.User;
import org.example.model.User.UserRole;
import org.example.util.DBConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
            Assertions.fail("Database connection failed during setup/cleanup: " + e.getMessage());
        }
    }
    @Test
    void testAddUser_DuplicateEmailFailure() {
        userDAO.addUser(TEST_USER);
        User duplicateUser = new User("uid2", "Another Name", TEST_EMAIL, "anotherpass", UserRole.MANAGER);
        boolean result = userDAO.addUser(duplicateUser);
        Assertions.assertFalse(result, "Adding a user with a duplicate email should return false.");
    }

    @Test
    void testLogin_Success() {
        userDAO.addUser(TEST_USER);
        User loggedInUser = userDAO.login(TEST_EMAIL, TEST_PASS);

        Assertions.assertNotNull(loggedInUser, "Login should succeed with correct credentials.");
        Assertions.assertEquals(TEST_USER.getEmail(), loggedInUser.getEmail());
        Assertions.assertEquals(TEST_USER.getRole(), loggedInUser.getRole());
    }

    @Test
    void testLogin_Failure_IncorrectPassword() {
        userDAO.addUser(TEST_USER);
        User loggedInUser = userDAO.login(TEST_EMAIL, "wrongpassword");
        Assertions.assertNull(loggedInUser, "Login should fail with an incorrect password.");
    }

    @Test
    void testLogin_Failure_UserNotFound() {
        User loggedInUser = userDAO.login("nonexistent@email.com", TEST_PASS);
        Assertions.assertNull(loggedInUser, "Login should fail if the user email does not exist.");
    }
    @Test
    void testAddUser_Success() {
        User newUser = new User("uid456", "New User", "new.user@corp.com", "newpass", UserRole.MANAGER);

        boolean result = userDAO.addUser(newUser);
        Assertions.assertTrue(result, "Adding a new unique user should succeed.");
        Optional<User> retrievedUser = userDAO.getUserById("uid456");
        assertTrue(retrievedUser.isPresent(), "User must be retrievable after successful addition.");
    }

    @Test
    void testIsAuthorized_RoleCheck() {
        userDAO.addUser(TEST_USER);
        userDAO.login(TEST_EMAIL, TEST_PASS);
        boolean authorizedAsViewer = userDAO.isAuthorized(UserRole.VIEWER);
        Assertions.assertTrue(authorizedAsViewer, "VIEWER should be authorized for VIEWER role.");
        boolean authorizedAsManager = userDAO.isAuthorized(UserRole.MANAGER);
        Assertions.assertFalse(authorizedAsManager, "VIEWER should NOT be authorized for MANAGER role.");
        userDAO.logout();
        boolean unauthorizedCheck = userDAO.isAuthorized(UserRole.VIEWER);
        Assertions.assertFalse(unauthorizedCheck, "No user logged in should result in unauthorized.");
    }

    @Test
    void testUpdateUser_Success() {
        userDAO.addUser(TEST_USER);
        String newName = "Updated Test User";
        UserRole newRole = UserRole.MANAGER;
        User updatedUser = new User(TEST_ID, newName, TEST_EMAIL, TEST_PASS, newRole);

        boolean result = userDAO.updateUser(updatedUser);
        Assertions.assertTrue(result, "Updating an existing user should return true.");
        User retrievedUser = userDAO.getUserById(TEST_ID).orElseThrow();
        Assertions.assertEquals(newName, retrievedUser.getFullName(), "User name must be updated.");
        Assertions.assertEquals(newRole, retrievedUser.getRole(), "User role must be updated.");
    }

    @Test
    void testDeleteUser_Success() {
        userDAO.addUser(TEST_USER);
        boolean deleteResult = userDAO.deleteUser(TEST_ID);
        Assertions.assertTrue(deleteResult, "Deletion of an existing user should return true.");

        Optional<User> retrievedUser = userDAO.getUserById(TEST_ID);
        Assertions.assertFalse(retrievedUser.isPresent(), "User should not be retrievable after being deleted.");
    }

}