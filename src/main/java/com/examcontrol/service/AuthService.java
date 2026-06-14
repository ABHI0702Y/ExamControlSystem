package com.examcontrol.service;

import com.examcontrol.dao.UserDAO;
import com.examcontrol.model.User;
import com.examcontrol.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static AuthService instance;

    private final UserDAO userDAO = new UserDAO();
    private User currentUser;

    private AuthService() {}

    public static synchronized AuthService getInstance() {
        if (instance == null) instance = new AuthService();
        return instance;
    }

    /** Returns the logged-in user, or empty on failure. */
    public Optional<User> login(String username, String password) {
        if (username == null || password == null) return Optional.empty();

        Optional<User> opt = userDAO.findByUsername(username.trim());
        if (opt.isEmpty()) {
            log.warn("Login attempt for unknown user: {}", username);
            return Optional.empty();
        }

        User user = opt.get();
        if (!user.isActive()) {
            log.warn("Login attempt for inactive user: {}", username);
            return Optional.empty();
        }

        if (!PasswordUtil.verify(password, user.getPasswordHash())) {
            log.warn("Bad password for user: {}", username);
            return Optional.empty();
        }

        currentUser = user;
        log.info("User logged in: {} ({})", user.getUsername(), user.getRole());
        return Optional.of(user);
    }

    public void logout() {
        if (currentUser != null) {
            log.info("User logged out: {}", currentUser.getUsername());
        }
        currentUser = null;
    }

    public User getCurrentUser()        { return currentUser; }
    public boolean isLoggedIn()         { return currentUser != null; }
    public boolean isAdmin()            { return isLoggedIn() && currentUser.getRole().name().equals("ADMIN"); }
    public boolean isTeacher()          { return isLoggedIn() && currentUser.getRole().name().equals("TEACHER"); }
    public boolean isStudent()          { return isLoggedIn() && currentUser.getRole().name().equals("STUDENT"); }
}
