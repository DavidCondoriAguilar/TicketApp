package com.tickets.ravetix.service.impl;

import com.tickets.ravetix.dto.mapper.PaymentMapper;
import com.tickets.ravetix.dto.payment.PaymentRequestDTO;
import com.tickets.ravetix.dto.payment.PaymentResponseDTO;
import com.tickets.ravetix.entity.Payment;
import com.tickets.ravetix.entity.Ticket;
import com.tickets.ravetix.entity.User;
import com.tickets.ravetix.entity.Zone;
import com.tickets.ravetix.entity.EventHistory;
import com.tickets.ravetix.enums.EstadoPago;
import com.tickets.ravetix.enums.TicketState;
import com.tickets.ravetix.exception.ResourceNotFoundException;
import com.tickets.ravetix.exception.ValidationException;
import com.tickets.ravetix.repository.PaymentRepository;
import com.tickets.ravetix.repository.TicketRepository;
import com.tickets.ravetix.repository.UserRepository;
import com.tickets.ravetix.repository.EventHistoryRepository;
import com.tickets.ravetix.service.interfac.PaymentService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.time.LocalDateTime;
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
    private final EventHistoryRepository eventHistoryRepository;

    /**
     * Crea un nuevo pago para un ticket, validando la existencia del usuario y ticket, el estado del ticket y el monto.
     * El pago se crea inicialmente en estado PENDIENTE y luego se procesa automáticamente.
     *
     * @param paymentDTO Objeto de transferencia con los datos del pago a crear.
     * @return PaymentResponseDTO con la información del pago procesado.
     * @throws ValidationException si el usuario, ticket no existen, el ticket ya está pagado, el monto es inválido o no coincide con el precio del ticket.
     */
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

    /**
     * Obtiene la información de un pago por su identificador único (UUID).
     *
     * @param id Identificador único del pago.
     * @return PaymentResponseDTO con los datos del pago encontrado.
     * @throws ResourceNotFoundException si el pago no existe.
     */
    @Override
    @Transactional
    public PaymentResponseDTO getPaymentById(UUID id) {
        log.info("Fetching payment with ID: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        return paymentMapper.toDto(payment);
    }

    /**
     * Obtiene una lista paginada de pagos realizados por un usuario específico.
     *
     * @param userId Identificador único del usuario.
     * @param pageable Parámetro de paginación y ordenamiento.
     * @return Page<PaymentResponseDTO> página de pagos encontrados.
     * @throws ResourceNotFoundException si el usuario no existe.
     */
    @Override
    @Transactional
    public Page<PaymentResponseDTO> getPaymentsByUserId(UUID userId, Pageable pageable) {
        log.info("Fetching payments for user ID: {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return paymentRepository.findByUsuarioId(userId, pageable)
                .map(paymentMapper::toDto);
    }

    /**
     * Obtiene una lista paginada de pagos asociados a un ticket específico.
     *
     * @param ticketId Identificador único del ticket.
     * @param pageable Parámetro de paginación y ordenamiento.
     * @return Page<PaymentResponseDTO> página de pagos encontrados.
     * @throws ResourceNotFoundException si el ticket no existe.
     */
    @Override
    @Transactional
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

    /**
     * Procesa un pago existente, validando su estado y la disponibilidad de la zona y evento asociados al ticket.
     * Actualiza el estado del pago y del ticket, y registra el evento en el historial.
     *
     * @param paymentId Identificador único del pago a procesar.
     * @return PaymentResponseDTO con la información del pago procesado.
     * @throws ResourceNotFoundException si el pago no existe.
     * @throws ValidationException si el pago ya fue procesado, el ticket ya está pagado, no hay entradas disponibles o hay errores en la información relacionada.
     */
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
            
            // Crear entrada en el historial de eventos
            createEventHistory(ticket);
            
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
    
    private void createEventHistory(Ticket ticket) {
        try {
            User user = userRepository.findById(ticket.getUsuario().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", ticket.getUsuario().getId()));
                    
            EventHistory eventHistory = new EventHistory();
            eventHistory.setEvento(ticket.getEvento());
            eventHistory.setUsuario(user);
            eventHistory.setAsistenciaConfirmada(false);
            eventHistory.setFechaConfirmacionAsistencia(LocalDateTime.now());
            
            eventHistoryRepository.save(eventHistory);
            log.info("Created event history entry for user ID: {} and event ID: {}", 
                    user.getId(), ticket.getEvento().getId());
                    
        } catch (Exception e) {
            log.error("Error creating event history entry: {}", e.getMessage(), e);
            // No lanzamos excepción para no afectar el flujo de pago
        }
    }

    /**
     * Procesa el reembolso de un pago completado, actualizando el estado del pago y del ticket asociado.
     *
     * @param paymentId Identificador único del pago a reembolsar.
     * @param reason Motivo del reembolso.
     * @return PaymentResponseDTO con la información del pago reembolsado.
     * @throws ResourceNotFoundException si el pago no existe.
     * @throws ValidationException si el pago no está en estado COMPLETADO.
     */
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
