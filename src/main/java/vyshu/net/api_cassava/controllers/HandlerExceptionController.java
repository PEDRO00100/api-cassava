package vyshu.net.api_cassava.controllers;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> globalErrorHandler(Exception ex) {
        ErrorDto error = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error",
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(error);
    }
}
