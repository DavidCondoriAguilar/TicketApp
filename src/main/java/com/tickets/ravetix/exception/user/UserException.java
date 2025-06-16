package com.tickets.ravetix.exception.user;

import com.tickets.ravetix.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when there is an error related to User operations.
 */
public class UserException extends BaseException {
    
    public UserException(String message, String details) {
        super(
            HttpStatus.BAD_REQUEST,
            "USER_ERROR",
            message,
            details
        );
    }
    
    public UserException(String message, String details, HttpStatus status) {
        super(
            status,
            "USER_ERROR",
            message,
            details
        );
    }
}
