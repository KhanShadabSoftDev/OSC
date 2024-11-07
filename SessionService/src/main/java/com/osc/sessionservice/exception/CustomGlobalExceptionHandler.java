package com.osc.sessionservice.exception;

import com.osc.sessionservice.response.ApiResponse;
import com.osc.sessionservice.response.StatusCode;
import jakarta.validation.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomGlobalExceptionHandler {
    private static final Logger log = LogManager.getLogger(CustomGlobalExceptionHandler.class);


    @ExceptionHandler(ActiveSessionException.class)
    public ResponseEntity<ApiResponse> handleEmailIdAlreadyExistsException(ActiveSessionException ex) {
        log.error("Session is Already Active: {}", ex.getMessage());

        ApiResponse apiResponse = new ApiResponse(StatusCode.ACTIVE_SESSION_EXISTS,null);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_ACCEPTABLE);
    }
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiResponse> handleUserRegistrationFailedException(InvalidPasswordException ex) {

        ApiResponse apiResponse = new ApiResponse(StatusCode.INVALID_PASSWORD, null);
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidUserIDException.class)
    public ResponseEntity<ApiResponse> handleUserNotFoundException(InvalidUserIDException ex) {

        ApiResponse apiResponse = new ApiResponse(StatusCode.INVALID_USER_ID, null);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(gRPCCallException.class)
    public ResponseEntity<ApiResponse> handlegRPCCallException(gRPCCallException ex) {
        ApiResponse apiResponse = new ApiResponse(StatusCode.UNEXPECTED_ERROR, null);
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ex.getLocalizedMessage());
    }



    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException ( HttpMessageNotReadableException ex) {

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Request body is not readable. Please check the request and try again !!");
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return errors;
    }
}
