package vyshu.net.api_cassava.services;

import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import vyshu.net.api_cassava.repositories.UserRepository;
import vyshu.net.api_cassava.utils.JwtUtil;
import vyshu.net.api_cassava.utils.ValidateDataUsersUtil;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String register(String username, String email, String password, String device) {
        if (!ValidateDataUsersUtil.verifyPassword(password).equals("Valid")) {
            throw new IllegalArgumentException(ValidateDataUsersUtil.verifyPassword(password));
        }
        String encodedPassword = passwordEncoder.encode(password);
        userRepository.registerUser(username, email, encodedPassword);

        Optional<Map<String, Object>> userOptional = userRepository.findByEmailOrUsername(email);
        if (userOptional.isEmpty()) {
            throw new IllegalStateException("Error retrieving user after registration");
        }

        Map<String, Object> user = userOptional.get();
        return jwtUtil.generateToken(
                (String) user.get("username"),
                (String) user.get("email"),
                (String) user.getOrDefault("role", "user"),
                device);
    }

    public String login(String identifier, String password, String device) {
        Optional<Map<String, Object>> userOptional = userRepository.findByEmailOrUsername(identifier);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Credentials do not match");
        }

        Map<String, Object> user = userOptional.get();

        if (!passwordEncoder.matches(password, (String) user.get("password"))) {
            throw new IllegalArgumentException("Credentials do not match");
        }

        userRepository.updateLastConnection((String) user.get("email"));
        return jwtUtil.generateToken(
                (String) user.get("username"),
                (String) user.get("email"),
                (String) user.get("role"),
                device);
    }
}