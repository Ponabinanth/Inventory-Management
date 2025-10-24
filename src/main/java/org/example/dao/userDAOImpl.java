package org.example.dao;
import org.example.model.User;
import java.util.List;
import java.util.Optional;

public class userDAOImpl {

    private User currentUser = null;
    private final List<User> users = List.of(
            new User("1", " Manager", "abinanthmuthu2006@gmail.com", "admin123", User.UserRole.MANAGER),
            new User("1", "Bob Viewer", "bob@corp.com", "viewer123", User.UserRole.VIEWER)
    );

    public Optional<User> getUserById(String userId) {
        return users.stream().filter(u -> u.getUserId().equals(userId)).findFirst();
    }

    public List<User> getAllUsers() {
        return users;
    }

    public boolean updateUser(User user) {
        System.out.println("DAO: User update simulated.");
        return true;
    }

    public boolean deleteUser(String userId) {
        System.out.println("DAO: User deletion simulated.");
        return true;
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
        return currentUser != null && currentUser.getRole().ordinal() >= requiredRole.ordinal();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }
}