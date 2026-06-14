package com.examcontrol.service;

import com.examcontrol.dao.UserDAO;
import com.examcontrol.model.Role;
import com.examcontrol.model.User;
import com.examcontrol.util.PasswordUtil;
import com.examcontrol.util.ValidationUtil;

import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    public List<User> getAllUsers()                      { return userDAO.findAll(); }
    public List<User> getUsersByRole(Role role)          { return userDAO.findByRole(role); }
    public Optional<User> getUserById(int id)           { return userDAO.findById(id); }
    public int countByRole(Role role)                   { return userDAO.countByRole(role); }

    public ServiceResult createUser(String username, String password, String fullName,
                                    String email, Role role) {
        if (ValidationUtil.isBlank(username))   return ServiceResult.error("Username is required.");
        if (!ValidationUtil.isValidUsername(username)) return ServiceResult.error("Username must be 3–50 alphanumeric chars.");
        if (ValidationUtil.isBlank(password))   return ServiceResult.error("Password is required.");
        if (!ValidationUtil.isStrongPassword(password)) return ServiceResult.error("Password needs uppercase, lowercase & digit (min 6 chars).");
        if (ValidationUtil.isBlank(fullName))   return ServiceResult.error("Full name is required.");
        if (!ValidationUtil.isValidEmail(email)) return ServiceResult.error("Invalid email address.");
        if (role == null)                        return ServiceResult.error("Role is required.");

        if (userDAO.existsByUsername(username)) return ServiceResult.error("Username already taken.");
        if (userDAO.existsByEmail(email))       return ServiceResult.error("Email already registered.");

        User user = new User();
        user.setUsername(username.trim());
        user.setPasswordHash(PasswordUtil.hash(password));
        user.setFullName(fullName.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setRole(role);
        user.setActive(true);

        return userDAO.create(user)
            ? ServiceResult.success("User created successfully.")
            : ServiceResult.error("Database error creating user.");
    }

    public ServiceResult updateUser(User user) {
        if (ValidationUtil.isBlank(user.getFullName())) return ServiceResult.error("Full name is required.");
        if (!ValidationUtil.isValidEmail(user.getEmail())) return ServiceResult.error("Invalid email.");

        Optional<User> existing = userDAO.findByUsername(user.getUsername());
        if (existing.isPresent() && existing.get().getId() != user.getId()) {
            return ServiceResult.error("Username taken by another user.");
        }
        return userDAO.update(user)
            ? ServiceResult.success("User updated.")
            : ServiceResult.error("Database error updating user.");
    }

    public ServiceResult changePassword(int userId, String currentPassword, String newPassword) {
        Optional<User> opt = userDAO.findById(userId);
        if (opt.isEmpty()) return ServiceResult.error("User not found.");
        if (!PasswordUtil.verify(currentPassword, opt.get().getPasswordHash()))
            return ServiceResult.error("Current password is incorrect.");
        if (!ValidationUtil.isStrongPassword(newPassword))
            return ServiceResult.error("New password needs uppercase, lowercase & digit (min 6 chars).");
        return userDAO.updatePassword(userId, PasswordUtil.hash(newPassword))
            ? ServiceResult.success("Password changed.")
            : ServiceResult.error("Database error changing password.");
    }

    public ServiceResult resetPassword(int userId, String newPassword) {
        if (!ValidationUtil.isStrongPassword(newPassword))
            return ServiceResult.error("Password needs uppercase, lowercase & digit (min 6 chars).");
        return userDAO.updatePassword(userId, PasswordUtil.hash(newPassword))
            ? ServiceResult.success("Password reset.")
            : ServiceResult.error("Database error resetting password.");
    }

    public ServiceResult deleteUser(int userId, int currentUserId) {
        if (userId == currentUserId) return ServiceResult.error("Cannot delete yourself.");
        return userDAO.deleteById(userId)
            ? ServiceResult.success("User deleted.")
            : ServiceResult.error("Database error deleting user.");
    }

    public record ServiceResult(boolean success, String message) {
        static ServiceResult success(String msg) { return new ServiceResult(true, msg); }
        static ServiceResult error(String msg)   { return new ServiceResult(false, msg); }
    }
}
