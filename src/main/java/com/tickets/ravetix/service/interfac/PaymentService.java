package com.tickets.ravetix.service.interfac;

import com.tickets.ravetix.dto.payment.PaymentRequestDTO;
import com.tickets.ravetix.dto.payment.PaymentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PaymentService {
    
    /**
     * Create a new payment
     * @param paymentDTO payment data
     * @return created payment
     */
    PaymentResponseDTO createPayment(PaymentRequestDTO paymentDTO);
    
    /**
     * Get payment by ID
     * @param id payment ID
     * @return payment details
     */
    PaymentResponseDTO getPaymentById(UUID id);
    
    /**
     * Get all payments for a user
     * @param userId user ID
     * @param pageable pagination information
     * @return page of payments
     */
    Page<PaymentResponseDTO> getPaymentsByUserId(UUID userId, Pageable pageable);
    
    /**
     * Get all payments for a ticket
     * @param ticketId ticket ID
     * @return list of payments
     */
    Page<PaymentResponseDTO> getPaymentsByTicketId(UUID ticketId, Pageable pageable);
    
    /**
     * Process a payment
     * @param paymentId payment ID
     * @return processed payment
     */
    PaymentResponseDTO processPayment(UUID paymentId);
    
    /**
     * Refund a payment
     * @param paymentId payment ID
     * @param reason refund reason
     * @return refunded payment
     */
    PaymentResponseDTO refundPayment(UUID paymentId, String reason);
}
