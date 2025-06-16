package com.tickets.ravetix.exception.event;

import com.tickets.ravetix.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when there is an error related to Event operations.
 */
public class EventException extends BaseException {
    
    public EventException(String message, String details) {
        super(
            HttpStatus.BAD_REQUEST,
            "EVENT_ERROR",
            message,
            details
        );
    }
    
    public EventException(String message, String details, HttpStatus status) {
        super(
            status,
            "EVENT_ERROR",
            message,
            details
        );
    }
}
