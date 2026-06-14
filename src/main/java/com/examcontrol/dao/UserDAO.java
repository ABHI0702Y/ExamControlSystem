package com.examcontrol.dao;

import com.examcontrol.config.DatabaseConfig;
import com.examcontrol.model.Role;
import com.examcontrol.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {

    private static final Logger log = LoggerFactory.getLogger(UserDAO.class);

    private Connection conn() throws SQLException {
        return DatabaseConfig.getInstance().getConnection();
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            log.error("findByUsername failed", e);
        }
        return Optional.empty();
    }

    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            log.error("findById failed", e);
        }
        return Optional.empty();
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY role, full_name";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) users.add(map(rs));
        } catch (SQLException e) {
            log.error("findAll failed", e);
        }
        return users;
    }

    public List<User> findByRole(Role role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY full_name";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, role.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) users.add(map(rs));
            }
        } catch (SQLException e) {
            log.error("findByRole failed", e);
        }
        return users;
    }

    public boolean create(User user) {
        String sql = """
            INSERT INTO users (username, password_hash, full_name, email, role, is_active)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getRole().name());
            ps.setBoolean(6, user.isActive());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) user.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            log.error("create user failed", e);
        }
        return false;
    }

    public boolean update(User user) {
        String sql = """
            UPDATE users SET full_name=?, email=?, role=?, is_active=?
            WHERE id=?
            """;
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getRole().name());
            ps.setBoolean(4, user.isActive());
            ps.setInt(5, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("update user failed", e);
        }
        return false;
    }

    public boolean updatePassword(int userId, String newHash) {
        String sql = "UPDATE users SET password_hash=? WHERE id=?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("updatePassword failed", e);
        }
        return false;
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM users WHERE id=?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("deleteById failed", e);
        }
        return false;
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM users WHERE username=?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            log.error("existsByUsername failed", e);
        }
        return false;
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM users WHERE email=?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            log.error("existsByEmail failed", e);
        }
        return false;
    }

    public int countByRole(Role role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role=?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, role.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("countByRole failed", e);
        }
        return 0;
    }

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setRole(Role.fromString(rs.getString("role")));
        u.setActive(rs.getBoolean("is_active"));
        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) u.setCreatedAt(ca.toLocalDateTime());
        Timestamp ua = rs.getTimestamp("updated_at");
        if (ua != null) u.setUpdatedAt(ua.toLocalDateTime());
        return u;
    }
}
