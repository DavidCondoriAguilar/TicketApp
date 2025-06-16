package com.tickets.ravetix.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a resource cannot be deleted because it's being used by other resources.
 */
public class ResourceInUseException extends BaseException {
    
    public ResourceInUseException(String resourceName, String fieldName, Object fieldValue) {
        super(
            HttpStatus.CONFLICT,
            "RESOURCE_IN_USE",
            String.format("%s with %s '%s' cannot be deleted because it's in use", resourceName, fieldName, fieldValue),
            String.format("The %s is being referenced by other resources and cannot be deleted", resourceName.toLowerCase())
        );
    }
    
    public ResourceInUseException(String message, String details) {
        super(
            HttpStatus.CONFLICT,
            "RESOURCE_IN_USE",
            message,
            details
        );
    }
}
