package vyshu.net.api_cassava.services;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import vyshu.net.api_cassava.exceptions.AuthException;
import vyshu.net.api_cassava.repositories.UserRepository;
import vyshu.net.api_cassava.utils.JwtUtil;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public String register(String username, String email, String password, String device) {
        userRepository.saveNewUser(username, email, password);
        Optional<Map<String, Object>> userOptional = userRepository.findByIdentifier(email);
        if (userOptional.isEmpty()) {
            throw new AuthException("Error retrieving user after registration");
        }
        Map<String, Object> user = userOptional.get();
        String tokenId = UUID.randomUUID().toString();

        String token = jwtUtil.generateToken(
                (String) user.get("username"),
                (String) user.get("email"),
                (String) user.getOrDefault("role", "user"),
                device, tokenId);
        userRepository.saveToken(email, token, device, tokenId);
        return token;
    }

    public String login(String identifier, String password, String device) {
        Optional<Map<String, Object>> userOptional = userRepository.findByIdentifier(identifier);

        if (userOptional.isEmpty() || !userRepository.validatePassword(identifier, password)) {
            throw new AuthException("Credentials do not match");
        }
        Map<String, Object> user = userOptional.get();

        // Verificar si la contraseña necesita ser actualizada
        String storedPassword = (String) user.get("password");
        if (storedPassword.startsWith("$2a$10$")) {
            // Re-codificar la contraseña con el nuevo factor de costo
            String newEncodedPassword = passwordEncoder.encode(password);
            userRepository.updatePassword(identifier, newEncodedPassword);
        }

        userRepository.updateLastConnection((String) user.get("email"));
        String tokenId = UUID.randomUUID().toString();
        String token = jwtUtil.generateToken(
                (String) user.get("username"),
                (String) user.get("email"),
                (String) user.getOrDefault("role", "user"),
                device, tokenId);
        userRepository.saveToken((String) user.get("email"), token, device, tokenId);
        return token;
    }
}