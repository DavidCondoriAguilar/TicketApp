package com.tickets.ravetix.exception.zone;

import com.tickets.ravetix.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when there is an error related to Zone operations.
 */
public class ZoneException extends BaseException {
    
    public ZoneException(String message, String details) {
        super(
            HttpStatus.BAD_REQUEST,
            "ZONE_ERROR",
            message,
            details
        );
    }
    
    public ZoneException(String message, String details, HttpStatus status) {
        super(
            status,
            "ZONE_ERROR",
            message,
            details
        );
    }
}
