package vyshu.net.api_cassava.utils;

import org.springframework.stereotype.Component;
import vyshu.net.api_cassava.repositories.UserRepository;

import java.util.regex.Pattern;

@Component
public class ValidateDataUsersUtil {

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private static final Pattern patternUsername = Pattern.compile("^[A-Za-z0-9+_.-]+$");

    public String verifyEmail(String email, UserRepository userRepository) {
        if (email == null || email.isEmpty()) {
            return "Email cannot be empty";
        } else if (email.length() > 100) {
            return "Email too long, must be at most 100 characters";
        } else if (userRepository.existsByEmail(email)) {
            return "Email already exists";
        } else if (!pattern.matcher(email).matches()) {
            return "Invalid email format";
        } else {
            return "Valid";
        }
    }

    public String verifyUsername(String username, UserRepository userRepository) {
        if (username == null || username.isEmpty()) {
            return "Username cannot be empty";
        } else if (username.length() > 20) {
            return "Username too long, must be at most 20 characters";
        } else if (userRepository.existsByUsername(username)) {
            return "Username already exists";
        } else if (!patternUsername.matcher(username).matches()) {
            return "Invalid username format";
        } else {
            return "Valid";
        }
    }

    public String verifyPassword(String password) {
        if (password == null || password.isEmpty()) {
            return "Password cannot be empty";
        } else if (password.length() < 10) {
            return "Password too short, must be at least 10 characters";
        } else if (password.length() > 60) {
            return "Password too long, must be at most 60 characters";
        } else if (password.contains(" ")) {
            return "Password cannot contain spaces";
        } else {
            return "Valid";
        }
    }
}