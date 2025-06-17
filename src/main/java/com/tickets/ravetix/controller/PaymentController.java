package com.tickets.ravetix.controller;

import com.tickets.ravetix.dto.payment.PaymentRequestDTO;
import com.tickets.ravetix.dto.payment.PaymentResponseDTO;
import com.tickets.ravetix.service.interfac.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPayment(
            @Valid @RequestBody PaymentRequestDTO paymentDTO) {
        PaymentResponseDTO createdPayment = paymentService.createPayment(paymentDTO);
        return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PaymentResponseDTO>> getPaymentsByUserId(
            @PathVariable UUID userId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(paymentService.getPaymentsByUserId(userId, pageable));
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<Page<PaymentResponseDTO>> getPaymentsByTicketId(
            @PathVariable UUID ticketId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(paymentService.getPaymentsByTicketId(ticketId, pageable));
    }

    @PostMapping("/{paymentId}/process")
    public ResponseEntity<PaymentResponseDTO> processPayment(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(paymentService.processPayment(paymentId));
    }

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentResponseDTO> refundPayment(
            @PathVariable UUID paymentId,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(paymentService.refundPayment(paymentId, reason));
    }
}
