package com.tickets.ravetix.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a business rule is violated.
 */
public class BusinessRuleException extends BaseException {
    
    public BusinessRuleException(String message) {
        super(
            HttpStatus.BAD_REQUEST,
            "BUSINESS_RULE_VIOLATION",
            message,
            "A business rule was violated: " + message
        );
    }
    
    public BusinessRuleException(String message, String details) {
        super(
            HttpStatus.BAD_REQUEST,
            "BUSINESS_RULE_VIOLATION",
            message,
            details
        );
    }
}
