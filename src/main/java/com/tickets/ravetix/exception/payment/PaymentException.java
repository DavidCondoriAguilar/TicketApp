package com.tickets.ravetix.exception.payment;

import com.tickets.ravetix.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when there is an error related to Payment operations.
 */
public class PaymentException extends BaseException {
    
    public PaymentException(String message, String details) {
        super(
            HttpStatus.BAD_REQUEST,
            "PAYMENT_ERROR",
            message,
            details
        );
    }
    
    public PaymentException(String message, String details, HttpStatus status) {
        super(
            status,
            "PAYMENT_ERROR",
            message,
            details
        );
    }
}
