package com.tickets.ravetix.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when authentication fails.
 */
public class AuthenticationException extends BaseException {
    
    public AuthenticationException(String message) {
        super(
            HttpStatus.UNAUTHORIZED,
            "AUTHENTICATION_FAILED",
            "Authentication failed",
            message
        );
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(
            HttpStatus.UNAUTHORIZED,
            "AUTHENTICATION_FAILED",
            "Authentication failed",
            message,
            cause
        );
    }
}
