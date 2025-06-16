package com.tickets.ravetix.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception class for the application.
 * All custom exceptions should extend this class.
 */
@Getter
public abstract class BaseException extends RuntimeException {
    
    private final HttpStatus status;
    private final String errorCode;
    private final String details;
    
    protected BaseException(HttpStatus status, String errorCode, String message, String details) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
        this.details = details;
    }
    
    protected BaseException(HttpStatus status, String errorCode, String message, String details, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
        this.details = details;
    }
}
