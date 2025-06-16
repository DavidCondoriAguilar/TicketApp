package com.tickets.ravetix.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends BaseException {
    
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(
            HttpStatus.NOT_FOUND,
            "RESOURCE_NOT_FOUND",
            String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue),
            String.format("The requested %s with %s '%s' does not exist", resourceName, fieldName, fieldValue)
        );
    }
}
