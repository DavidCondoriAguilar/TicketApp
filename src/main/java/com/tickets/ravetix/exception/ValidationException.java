package com.tickets.ravetix.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * Exception thrown when data validation fails.
 */
public class ValidationException extends BaseException {
    
    public ValidationException(String message, String details) {
        super(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR",
            message,
            details
        );
    }
    
    public ValidationException(String message, String details, Map<String, String> validationErrors) {
        super(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR",
            message,
            details + ": " + validationErrors.toString()
        );
    }
}
