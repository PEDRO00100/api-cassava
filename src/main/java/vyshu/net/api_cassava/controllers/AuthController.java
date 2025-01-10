package vyshu.net.api_cassava.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vyshu.net.api_cassava.services.AuthService;
import vyshu.net.api_cassava.utils.JwtUtil;

@RestController
@RequestMapping("/auth/v1")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam(required = true) String identifier,
            @RequestParam(required = true) String password) {
        Map<String, String> response = new HashMap<>();
        try {
            response.put("Bearer", authService.login(identifier, password));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Error e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestParam(required = true) String username,
            @RequestParam(required = true) String email,
            @RequestParam(required = true) String password) {
        Map<String, String> response = new HashMap<>();
        try {
            response.put("Bearer", authService.register(username, email, password));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Error e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/validate/token")
    public ResponseEntity<Map<String, String>> validateToken(@RequestParam(required = true) String token) {
        Map<String, String> response = new HashMap<>();
        if (jwtUtil.validateToken(token)) {
            response.put("message", "Token is valid");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("message", "Token is invalid");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}