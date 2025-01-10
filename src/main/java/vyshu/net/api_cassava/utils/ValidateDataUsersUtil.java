package vyshu.net.api_cassava.utils;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class ValidateDataUsersUtil {

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public static String verifyEmail(String email) {
        if (pattern.matcher(email).matches()) {
            return "Valid";
        } else {
            return "Invalid email format";
        }
    }

    public static String verifyPassword(String password) {
        if (password.length() < 10) {
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