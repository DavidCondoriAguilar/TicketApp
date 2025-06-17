package com.tickets.ravetix.service.impl;

import com.tickets.ravetix.dto.mapper.PaymentMapper;
import com.tickets.ravetix.dto.payment.PaymentRequestDTO;
import com.tickets.ravetix.dto.payment.PaymentResponseDTO;
import com.tickets.ravetix.entity.Payment;
import com.tickets.ravetix.entity.Ticket;
import com.tickets.ravetix.entity.User;
import com.tickets.ravetix.entity.Zone;
import com.tickets.ravetix.enums.EstadoPago;
import com.tickets.ravetix.enums.TicketState;
import com.tickets.ravetix.exception.ResourceNotFoundException;
import com.tickets.ravetix.exception.ValidationException;
import com.tickets.ravetix.repository.PaymentRepository;
import com.tickets.ravetix.repository.TicketRepository;
import com.tickets.ravetix.repository.UserRepository;
import com.tickets.ravetix.service.interfac.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponseDTO createPayment(PaymentRequestDTO paymentDTO) {
        log.info("Creating payment for ticket: {}", paymentDTO.getTicketId());
        
        // Validar que el usuario exista
        User user = userRepository.findById(paymentDTO.getUsuarioId())
                .orElseThrow(() -> new ValidationException("Usuario no encontrado", "No se encontró el usuario con ID: " + paymentDTO.getUsuarioId()));
        
        // Validar que el ticket exista
        Ticket ticket = ticketRepository.findById(paymentDTO.getTicketId())
                .orElseThrow(() -> new ValidationException("Ticket no encontrado", "No se encontró el ticket con ID: " + paymentDTO.getTicketId()));
        
        // Validar que el ticket no esté ya pagado
        if (ticket.getEstado() == TicketState.PAGADO) {
            throw new ValidationException("Validación fallida", "El ticket ya ha sido pagado");
        }
        
        // Validar que el monto sea válido
        if (paymentDTO.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Validación fallida", "El monto debe ser mayor a cero");
        }
        
        // Validar que el monto coincida con el precio del ticket
        if (paymentDTO.getMonto().compareTo(ticket.getPrecio()) != 0) {
            throw new ValidationException("Validación fallida", "El monto no coincide con el precio del ticket");
        }
        
        // Crear el pago con estado PENDIENTE por defecto
        Payment payment = new Payment();
        payment.setMonto(paymentDTO.getMonto());
        payment.setMetodoPago(paymentDTO.getMetodoPago());
        payment.setUsuario(user);
        payment.setTicket(ticket);
        payment.setEstado(EstadoPago.PENDIENTE); // Siempre se crea como PENDIENTE
        
        // Guardar el pago primero
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created successfully with ID: {}", savedPayment.getId());
        
        // Procesar el pago
        try {
            return processPayment(savedPayment.getId());
        } catch (Exception e) {
            // Si hay un error al procesar, marcar el pago como FALLIDO
            payment.setEstado(EstadoPago.FALLIDO);
            paymentRepository.save(payment);
            throw new ValidationException("Error al procesar el pago", e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(UUID id) {
        log.info("Fetching payment with ID: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        return paymentMapper.toDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> getPaymentsByUserId(UUID userId, Pageable pageable) {
        log.info("Fetching payments for user ID: {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return paymentRepository.findByUsuarioId(userId, pageable)
                .map(paymentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> getPaymentsByTicketId(UUID ticketId, Pageable pageable) {
        log.info("Fetching payments for ticket ID: {}", ticketId);
        if (!ticketRepository.existsById(ticketId)) {
            throw new ResourceNotFoundException("Ticket", "id", ticketId);
        }
        
        // Get all payments for the ticket
        List<Payment> payments = paymentRepository.findByTicketId(ticketId);
        
        // Apply pagination manually
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), payments.size());
        
        if (start > end) {
            return new PageImpl<>(Collections.emptyList(), pageable, payments.size());
        }
        
        List<Payment> paginatedPayments = payments.subList(start, end);
        List<PaymentResponseDTO> dtos = paginatedPayments.stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
                
        return new PageImpl<>(dtos, pageable, payments.size());
    }

    @Override
    @Transactional
    public PaymentResponseDTO processPayment(UUID paymentId) {
        log.info("Processing payment with ID: {}", paymentId);
        Payment payment = paymentRepository.findByIdWithTicketAndZone(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));
        
        // Verificar que el pago no esté ya procesado
        if (payment.getEstado() != EstadoPago.PENDIENTE) {
            throw new ValidationException("Validación fallida", "El pago ya ha sido procesado con estado: " + payment.getEstado());
        }
        
        try {
            // Aquí iría la lógica para procesar el pago con la pasarela de pago
            // Por ahora, simulamos un pago exitoso después de una pequeña pausa
            Thread.sleep(1000); // Simulamos un pequeño retardo de procesamiento
            
            // Verificar que el ticket aún no esté pagado
            Ticket ticket = payment.getTicket();
            if (ticket == null) {
                throw new ValidationException("Error en el pago", "No se encontró el ticket asociado al pago");
            }
            
            if (ticket.getEstado() == TicketState.PAGADO) {
                throw new ValidationException("Validación fallida", "El ticket ya ha sido pagado");
            }
            
            // Obtener la zona del ticket
            var zona = ticket.getZona();
            if (zona == null) {
                throw new ValidationException("Error en el pago", "No se encontró la zona asociada al ticket");
            }
            
            // Calcular entradas vendidas (solo tickets PAGADOS)
            long entradasVendidas = zona.getTickets().stream()
                .filter(t -> t.getEstado() == TicketState.PAGADO)
                .count();
                
            // Verificar que hay entradas disponibles
            if (entradasVendidas >= zona.getCapacidad()) {
                throw new ValidationException("Error en el pago", "No hay entradas disponibles en la zona seleccionada");
            }
            
            // Calcular capacidad total del evento sumando las capacidades de todas las zonas
            int capacidadTotalEvento = zona.getEvento().getZonas().stream()
                .mapToInt(Zone::getCapacidad)
                .sum();
                
            // Calcular entradas vendidas en todo el evento
            long totalEntradasVendidas = zona.getEvento().getZonas().stream()
                .flatMap(z -> z.getTickets().stream())
                .filter(t -> t.getEstado() == TicketState.PAGADO)
                .count();
                
            // Actualizar el estado del ticket
            ticket.setEstado(TicketState.PAGADO);
            ticketRepository.save(ticket);
            
            // Actualizar el estado del pago a COMPLETADO
            payment.setEstado(EstadoPago.COMPLETADO);
            paymentRepository.save(payment);
            
            // Actualizar el estado del ticket a PAGADO
            ticket.setEstado(TicketState.PAGADO);
            ticketRepository.save(ticket);
            
            log.info("Payment processed successfully for ID: {}", paymentId);
            return paymentMapper.toDto(payment);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error al procesar el pago: {}", e.getMessage(), e);
            payment.setEstado(EstadoPago.FALLIDO);
            paymentRepository.save(payment);
            throw new ValidationException("Error al procesar el pago", "Error en el procesamiento del pago");
        } catch (Exception e) {
            log.error("Error al procesar el pago: {}", e.getMessage(), e);
            payment.setEstado(EstadoPago.FALLIDO);
            paymentRepository.save(payment);
            throw new ValidationException("Error al procesar el pago", e.getMessage());
        }
    }

    @Override
    @Transactional
    public PaymentResponseDTO refundPayment(UUID paymentId, String reason) {
        log.info("Processing refund for payment ID: {}", paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));
        
        // Verificar que el pago esté aprobado
        if (payment.getEstado() != EstadoPago.COMPLETADO) {
            throw new ValidationException("Validación fallida", "Solo se pueden reembolsar pagos completados");
        }
        
        // Aquí iría la lógica para procesar el reembolso con la pasarela de pago
        // Por ahora, simulamos un reembolso exitoso
        payment.setEstado(EstadoPago.REEMBOLSADO);
        
        // Actualizar el estado del ticket
        Ticket ticket = payment.getTicket();
        ticket.setEstado(TicketState.CANCELADO);
        ticket.setMotivoCancelacion("Reembolso solicitado: " + reason);
        ticket = ticketRepository.save(ticket);
        
        Payment updatedPayment = paymentRepository.save(payment);
        log.info("Payment refunded successfully for ID: {}", paymentId);
        
        return paymentMapper.toDto(updatedPayment);
    }
}
