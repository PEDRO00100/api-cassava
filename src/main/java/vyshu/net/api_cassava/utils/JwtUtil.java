package vyshu.net.api_cassava.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import vyshu.net.api_cassava.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private static final long EXPIRATION_TIME = (1000 * 60 * 60 * 24) * 31L;
    private Key secretKey;

    @Value("${jwt.secret}")
    private String secret;

    private final UserRepository userRepository;

    public JwtUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(decodedKey);
    }

    public String generateToken(String username, String email, String role, String device, String UUID) {
        return Jwts.builder()
                .setSubject(username)
                .claim("email", email)
                .claim("role", role)
                .claim("device", device)
                .claim("UUID", UUID)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, "username");
    }

    public String extractEmail(String token) {
        return extractClaim(token, "email");
    }

    public String extractRole(String token) {
        return extractClaim(token, "role");
    }

    public String extractDevice(String token) {
        return extractClaim(token, "device");
    }

    public String extractUUID(String token) {
        return extractClaim(token, "UUID");
    }

    public boolean validateToken(String token) {
        String email = extractEmail(token);
        if (!userRepository.existsTokenByEmail(email, token)) {
            return false;
        }

        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            String UUID = extractUUID(token);
            userRepository.revokeTokenById(UUID);
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

    private String extractClaim(String token, String claimName) {
        return extractAllClaims(token).get(claimName, String.class);
    }
}