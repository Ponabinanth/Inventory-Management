package org.example.dao;
import org.example.model.User;
import org.example.model.User.UserRole;

public interface UserDAO {
    boolean addUser(User user);
    User login(String email, String password);
    User getUserByEmail(String email);
    User getCurrentUser();
    void logout();
    boolean isAuthorized(UserRole requiredRole);
}
