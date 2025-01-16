package vyshu.net.api_cassava.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import vyshu.net.api_cassava.repositories.UserRepository;
import vyshu.net.api_cassava.utils.JwtUtil;

@Service
public class UserDataService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserDataService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public String changePassword(String token, String oldPassword, String newPassword) {
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("Invalid token");
        }
        Optional<Map<String, Object>> userOptional = userRepository.findByIdentifier(jwtUtil.extractEmail(token));
        if (userOptional.isEmpty() || !userRepository.validatePassword(jwtUtil.extractEmail(token), oldPassword)) {
            throw new IllegalArgumentException("Credentials do not match");
        }
        String tokenId = UUID.randomUUID().toString();
        userRepository.updatePassword(jwtUtil.extractEmail(token), newPassword);
        String newToken = jwtUtil.generateToken(jwtUtil.extractUsername(token), jwtUtil.extractEmail(token),
                jwtUtil.extractRole(token), jwtUtil.extractDevice(token), tokenId);
        userRepository.saveToken(jwtUtil.extractEmail(token), newToken, jwtUtil.extractDevice(token), tokenId);
        userRepository.revokeTokenById(jwtUtil.extractUUID(token), jwtUtil.extractEmail(token));
        return newToken;
    }

    public String changeUsername(String token, String newUsername) {
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("Invalid token");
        }
        Optional<Map<String, Object>> userOptional = userRepository.findByIdentifier(jwtUtil.extractEmail(token));
        if (userOptional.isEmpty() || !userRepository.existsByUsername(jwtUtil.extractUsername(token))) {
            throw new IllegalArgumentException("Username does not exist");
        }
        String tokenId = UUID.randomUUID().toString();
        userRepository.updateUsername(jwtUtil.extractEmail(token), newUsername);
        String newToken = jwtUtil.generateToken(newUsername, jwtUtil.extractEmail(token),
                jwtUtil.extractRole(token), jwtUtil.extractDevice(token), tokenId);
        userRepository.saveToken(jwtUtil.extractEmail(token), newToken, jwtUtil.extractDevice(token), tokenId);
        userRepository.revokeTokenById(jwtUtil.extractUUID(token), jwtUtil.extractEmail(token));
        return newToken;
    }

    public List<String> getTokensID(String token) {
        token = token.trim();
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("Invalid token");
        }
        String email = jwtUtil.extractEmail(token);
        return userRepository.findAllTokensByEmail(email);
    }
}
