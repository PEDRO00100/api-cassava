package vyshu.net.api_cassava.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vyshu.net.api_cassava.services.UserDataService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/dashboard/v1/")
public class UserController {
    private final UserDataService userDataService;

    public UserController(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> postMethodName(@RequestParam(required = true) String token,
            @RequestParam(required = true) String oldPassword, @RequestParam(required = true) String newPassword) {
        Map<String, String> response = new HashMap<>();
        response.put("Bearer", userDataService.changePassword(token, oldPassword, newPassword));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/change-username")
    public ResponseEntity<Map<String, String>> changeUsername(@RequestParam(required = true) String token,
            @RequestParam(required = true) String newUsername) {
        Map<String, String> response = new HashMap<>();
        response.put("Bearer", userDataService.changeUsername(token, newUsername));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/get-tokens")
    public ResponseEntity<Map<String, Object>> getTokens(@RequestParam(required = true) String token) {
        Map<String, Object> response = new HashMap<>();
        response.put("tokens", userDataService.getTokensID(token));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}