package com.tickets.ravetix.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a user doesn't have permission to access a resource.
 */
public class AccessDeniedException extends BaseException {
    
    public AccessDeniedException() {
        super(
            HttpStatus.FORBIDDEN,
            "ACCESS_DENIED",
            "Access Denied",
            "You don't have permission to access this resource"
        );
    }
    
    public AccessDeniedException(String resource) {
        super(
            HttpStatus.FORBIDDEN,
            "ACCESS_DENIED",
            "Access Denied",
            String.format("You don't have permission to access this %s", resource)
        );
    }
}
