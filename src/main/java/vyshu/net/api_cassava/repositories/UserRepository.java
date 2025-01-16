package vyshu.net.api_cassava.repositories;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import vyshu.net.api_cassava.utils.ValidateDataUsersUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder encoder;
    private final ValidateDataUsersUtil validateDataUsersUtil;

    public UserRepository(JdbcTemplate jdbcTemplate, BCryptPasswordEncoder encoder,
            ValidateDataUsersUtil validateDataUsersUtil) {
        this.jdbcTemplate = jdbcTemplate;
        this.encoder = encoder;
        this.validateDataUsersUtil = validateDataUsersUtil;
    }

    public boolean existsByEmail(String email) {
        email = email.toLowerCase().trim();
        return Optional
                .ofNullable(
                        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE email = ?", Integer.class, email))
                .orElse(0) > 0;
    }

    public boolean existsByUsername(String username) {
        username = username.toLowerCase().trim();
        return Optional
                .ofNullable(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE username = ?", Integer.class,
                        username))
                .orElse(0) > 0;
    }

    public void updatePassword(String identifier, String newPassword) {
        newPassword = newPassword.trim();
        if (!"Valid".equals(validateDataUsersUtil.verifyPassword(newPassword))) {
            throw new IllegalArgumentException(validateDataUsersUtil.verifyPassword(newPassword));
        }
        jdbcTemplate.update("UPDATE users SET password = ? WHERE email = ? OR username = ?",
                encoder.encode(newPassword), identifier, identifier);
    }

    public void saveToken(String email, String token, String device, String tokenId) {
        email = email.toLowerCase().trim();
        token = token.trim();
        device = device.trim().toLowerCase();
        tokenId = tokenId.trim();
        jdbcTemplate.update("INSERT INTO user_tokens (email, token, device, token_id) VALUES (?, ?, ?, ?)", email,
                encoder.encode(token), device, tokenId);
    }

    public void saveNewUser(String username, String email, String password) {
        username = username.toLowerCase().trim();
        email = email.toLowerCase().trim();
        password = password.trim();
        String emailValidationResult = validateDataUsersUtil.verifyEmail(email, this);
        String usernameValidationResult = validateDataUsersUtil.verifyUsername(username, this);
        if (!"Valid".equals(emailValidationResult)) {
            throw new IllegalArgumentException(emailValidationResult);
        }
        if (!"Valid".equals(usernameValidationResult)) {
            throw new IllegalArgumentException(usernameValidationResult);
        }
        if (!"Valid".equals(validateDataUsersUtil.verifyPassword(password))) {
            throw new IllegalArgumentException(validateDataUsersUtil.verifyPassword(password));
        }
        jdbcTemplate.update("INSERT INTO users (username, email, password, last_connection) VALUES (?, ?, ?, NOW())",
                username, email, encoder.encode(password));
    }

    public void revokeTokenById(String tokenId, String email) {
        email = email.toLowerCase().trim();
        tokenId = tokenId.trim();
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user_tokens WHERE token_id = ? AND email = ?",
                Integer.class, tokenId, email);
        if (count != null && count > 0) {
            jdbcTemplate.update("DELETE FROM user_tokens WHERE token_id = ?", tokenId);
        } else {
            throw new IllegalArgumentException("Token does not belong to the provided email");
        }
    }

    public void updateUsername(String email, String newUsername) {
        newUsername = newUsername.toLowerCase().trim();
        String usernameValidationResult = validateDataUsersUtil.verifyUsername(newUsername, this);
        if (!"Valid".equals(usernameValidationResult)) {
            throw new IllegalArgumentException(usernameValidationResult);
        }
        jdbcTemplate.update("UPDATE users SET username = ? WHERE email = ?", newUsername, email);
    }

    public void updateLastConnection(String identifier) {
        identifier = identifier.toLowerCase().trim();
        jdbcTemplate.update("UPDATE users SET last_connection = NOW() WHERE email = ? OR username = ?", identifier,
                identifier);
    }

    public Optional<Map<String, Object>> findByIdentifier(String identifier) {
        identifier = identifier.toLowerCase().trim();
        try {
            return Optional.of(jdbcTemplate.queryForMap("SELECT * FROM users WHERE email = ? OR username = ?",
                    identifier, identifier));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean existsTokenByEmail(String email, String token) {
        try {
            String storedToken = jdbcTemplate.queryForObject("SELECT token FROM user_tokens WHERE email = ?",
                    String.class, email);
            return encoder.matches(token, storedToken);
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public List<String> findAllTokensByEmail(String email) {
        return jdbcTemplate.query("SELECT token_id FROM user_tokens WHERE email = ?", ps -> ps.setString(1, email),
                (rs, rowNum) -> rs.getString("token_id"));
    }

    public boolean validatePassword(String identifier, String password) {
        identifier = identifier.toLowerCase().trim();
        password = password.trim();
        try {
            String storedPassword = jdbcTemplate.queryForObject(
                    "SELECT password FROM users WHERE email = ? OR username = ?", String.class, identifier, identifier);
            return encoder.matches(password, storedPassword);
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }
}