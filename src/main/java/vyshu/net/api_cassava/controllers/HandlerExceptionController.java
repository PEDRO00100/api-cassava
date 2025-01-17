package vyshu.net.api_cassava.controllers;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import vyshu.net.api_cassava.exceptions.*;
import vyshu.net.api_cassava.models.ErrorDto;

@RestControllerAdvice
public class HandlerExceptionController {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorDto> noHandlerFound(Exception ex) {
        ErrorDto error = new ErrorDto(HttpStatus.NOT_FOUND.value(), "Resource not found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(error);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorDto> noFileError(Exception ex) {
        ErrorDto error = new ErrorDto(HttpStatus.NOT_FOUND.value(), "Resource not found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> illegalArgumentHandler(Exception ex) {
        ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST.value(), "Bad request", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorDto> methodNotAllowedHandler(Exception ex) {
        ErrorDto error = new ErrorDto(HttpStatus.METHOD_NOT_ALLOWED.value(), "Method not allowed", ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED.value()).body(error);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorDto> handleSignatureException(SignatureException ex) {
        ErrorDto error = new ErrorDto(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), "Invalid token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorDto> handleMalformedException(MalformedJwtException ex) {
        ErrorDto error = new ErrorDto(HttpStatus.UNAUTHORIZED.value(),ex.getMessage(),"Invalid token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        @ExceptionHandler(UserDataFormatExeption.class)
        public ResponseEntity<ErrorDto> handleUserDataFormatExeption(UserDataFormatExeption ex) {
        ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST.value(), "User data error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        @ExceptionHandler(AuthException.class)
        public ResponseEntity<ErrorDto> handleAuthException(AuthException ex) {
        ErrorDto error = new ErrorDto(HttpStatus.UNAUTHORIZED.value(), "Authentication error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorDto> globalErrorHandler(Exception ex) {
        ErrorDto error = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error",
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(error);
    }
}
