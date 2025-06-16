package com.tickets.ravetix.exception.ticket;

import com.tickets.ravetix.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when there is an error related to Ticket operations.
 */
public class TicketException extends BaseException {
    
    public TicketException(String message, String details) {
        super(
            HttpStatus.BAD_REQUEST,
            "TICKET_ERROR",
            message,
            details
        );
    }
    
    public TicketException(String message, String details, HttpStatus status) {
        super(
            status,
            "TICKET_ERROR",
            message,
            details
        );
    }
}
