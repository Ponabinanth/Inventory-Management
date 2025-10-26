package org.example.dao;
import org.example.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class userDAOImpl {

    private User currentUser = null;

    private final List<User> users = new ArrayList<>(List.of(
            new User("1", "Manager", "abinanthmuthu2006@gmail.com", "admin123", User.UserRole.MANAGER),
            new User("2", "Bob Viewer", "bob@corp.com", "viewer123", User.UserRole.VIEWER)
    ));
    public boolean addUser(User user) {
        if (users.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()))) {
            System.err.println("DAO Error: Email already exists.");
            return false;
        }
        users.add(user);
        System.out.println("DAO: User " + user.getUserId() + " added successfully.");
        return true;
    }

    public Optional<User> getUserById(String userId) {
        return users.stream().filter(u -> u.getUserId().equals(userId)).findFirst();
    }

    public Optional<User> getUserByEmail(String email) {
        return users.stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst();
    }

    public List<User> getAllUsers() {
        return users;
    }

    public boolean updateUser(User updatedUser) {
        Optional<User> existingUserOpt = getUserById(updatedUser.getUserId());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            users.remove(existingUser);
            users.add(updatedUser);
            System.out.println("DAO: User " + updatedUser.getUserId() + " updated simulated.");
            return true;
        }
        return false;
    }

    public boolean deleteUser(String userId) {
        boolean removed = users.removeIf(u -> u.getUserId().equals(userId));
        if (removed) {
            System.out.println("DAO: User " + userId + " deletion simulated.");
        }
        return removed;
    }

    public User login(String email, String password) {
        Optional<User> user = users.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password))
                .findFirst();
        if (user.isPresent()) {
            currentUser = user.get();
            return currentUser;
        }
        return null;
    }

    public boolean isAuthorized(User.UserRole requiredRole) {
        return currentUser == null || currentUser.getRole().ordinal() < requiredRole.ordinal();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }
    public boolean updateVerificationToken(String userId, String token) {
        boolean result = false;
        Optional<User> userOpt = getUserById(userId);
        if (userOpt.isPresent()) {
            System.out.println("DAO: Token update simulated for user " + userId);
            result = true;
        }
        return result;
    }
}