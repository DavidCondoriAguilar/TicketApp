package com.tickets.ravetix.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when trying to create a resource that already exists.
 */
public class ResourceAlreadyExistsException extends BaseException {
    
    public ResourceAlreadyExistsException(String resourceName, String fieldName, Object fieldValue) {
        super(
            HttpStatus.CONFLICT,
            "RESOURCE_ALREADY_EXISTS",
            String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue),
            String.format("Cannot create %s. A %s with %s '%s' already exists.", 
                resourceName, resourceName.toLowerCase(), fieldName, fieldValue)
        );
    }
}
