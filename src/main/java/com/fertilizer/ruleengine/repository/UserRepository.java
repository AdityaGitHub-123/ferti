package com.fertilizer.ruleengine.repository;

import com.fertilizer.ruleengine.entity.User;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

@Repository
public class UserRepository {
    private final DataSource dataSource;

    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public User save(User user) {
        String sql = "INSERT INTO users (username, email, phone, password, auth_token, profile_image) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getAuthToken());
            ps.setString(6, user.getProfileImage());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getLong(1));
                }
            }
            return user;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to save user", ex);
        }
    }

    public Optional<User> findByIdentifier(String identifier) {
        String sql = "SELECT * FROM users WHERE email = ? OR username = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, identifier);
            ps.setString(2, identifier);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUser(rs));
                }
                return Optional.empty();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to find user", ex);
        }
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUser(rs));
                }
                return Optional.empty();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to find user by email", ex);
        }
    }

    public Optional<User> findByToken(String token) {
        String sql = "SELECT * FROM users WHERE auth_token = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUser(rs));
                }
                return Optional.empty();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to find authenticated user", ex);
        }
    }

    public boolean existsByUsernameOrEmail(String username, String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? OR email = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to check user existence", ex);
        }
    }

    public void updateToken(Long userId, String token) {
        String sql = "UPDATE users SET auth_token = ? WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to update user token", ex);
        }
    }

    public void updatePassword(Long userId, String encryptedPassword) {
        String sql = "UPDATE users SET password = ?, auth_token = NULL WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, encryptedPassword);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to update password", ex);
        }
    }

    private User mapUser(ResultSet rs) throws Exception {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setPassword(rs.getString("password"));
        user.setAuthToken(rs.getString("auth_token"));
        user.setProfileImage(rs.getString("profile_image"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        return user;
    }
}
