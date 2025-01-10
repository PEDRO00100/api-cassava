package vyshu.net.api_cassava.repositories;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import vyshu.net.api_cassava.utils.ValidateDataUsersUtil;

import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    public void registerUser(String username, String email, String password) {
        if (existsByUsername(username) && existsByEmail(email)) {
            throw new IllegalArgumentException("Username and email are already taken");
        }
        if (existsByUsername(username)) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already registered");
        }
        if (!ValidateDataUsersUtil.verifyEmail(email).equals("Valid")) {
            throw new IllegalArgumentException(ValidateDataUsersUtil.verifyEmail(email));
        }
        String sql = "INSERT INTO users (username, email, password, last_connection) VALUES (?, ?, ?, NOW())";
        jdbcTemplate.update(sql, username, email, password);
    }

    public Optional<Map<String, Object>> findByEmailOrUsername(String identifier) {
        String sql = "SELECT * FROM users WHERE email = ? OR username = ?";
        try {
            return Optional.of(jdbcTemplate.queryForMap(sql, identifier, identifier));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void updateLastConnection(String email) {
        String sql = "UPDATE users SET last_connection = NOW() WHERE email = ?";
        jdbcTemplate.update(sql, email);
    }
}