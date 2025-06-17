package com.tickets.ravetix.service;

import com.tickets.ravetix.dto.mapper.PaymentMapper;
import com.tickets.ravetix.dto.payment.PaymentRequestDTO;
import com.tickets.ravetix.dto.payment.PaymentResponseDTO;
import com.tickets.ravetix.entity.*;
import com.tickets.ravetix.enums.EstadoPago;
import com.tickets.ravetix.enums.MetodoPago;
import com.tickets.ravetix.enums.TicketState;
import com.tickets.ravetix.exception.ResourceNotFoundException;
import com.tickets.ravetix.exception.ValidationException;
import com.tickets.ravetix.repository.PaymentRepository;
import com.tickets.ravetix.repository.TicketRepository;
import com.tickets.ravetix.repository.UserRepository;
import com.tickets.ravetix.repository.EventHistoryRepository;
import com.tickets.ravetix.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private EventHistoryRepository eventHistoryRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

   @Test
    void createPaymentShouldCreateAndProcessPaymentWhenValid() {
        UUID userId = UUID.randomUUID();
        UUID ticketId = UUID.randomUUID();
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setUsuarioId(userId);
        dto.setTicketId(ticketId);
        dto.setMonto(BigDecimal.TEN);
        dto.setMetodoPago(MetodoPago.TARJETA_CREDITO);

        User user = new User();
        user.setId(userId);

        // Mock zona y evento para el ticket
        Zone zone = new Zone();
        zone.setCapacidad(10);
        zone.setTickets(new ArrayList<>());
        Event event = new Event();
        event.setZonas(List.of(zone));
        zone.setEvento(event);

        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setEstado(TicketState.PENDIENTE_PAGO);
        ticket.setPrecio(BigDecimal.TEN);
        ticket.setZona(zone);

        Payment savedPayment = new Payment();
        UUID paymentId = UUID.randomUUID();
        savedPayment.setId(paymentId);
        savedPayment.setEstado(EstadoPago.PENDIENTE);
        savedPayment.setTicket(ticket);

        PaymentResponseDTO responseDTO = new PaymentResponseDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        when(paymentRepository.findByIdWithTicketAndZone(paymentId)).thenReturn(Optional.of(savedPayment));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        doReturn(responseDTO).when(paymentMapper).toDto(any(Payment.class));

        PaymentResponseDTO result = paymentService.createPayment(dto);

        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(ticketRepository).findById(ticketId);
        verify(paymentRepository, atLeastOnce()).save(any(Payment.class));
    }

    @Test
    void createPaymentShouldThrowWhenUserNotFound() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setUsuarioId(UUID.randomUUID());
        when(userRepository.findById(dto.getUsuarioId())).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> paymentService.createPayment(dto));
    }

    @Test
    void createPaymentShouldThrowWhenTicketNotFound() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setUsuarioId(UUID.randomUUID());
        dto.setTicketId(UUID.randomUUID());
        when(userRepository.findById(dto.getUsuarioId())).thenReturn(Optional.of(new User()));
        when(ticketRepository.findById(dto.getTicketId())).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> paymentService.createPayment(dto));
    }

    @Test
    void createPaymentShouldThrowWhenTicketAlreadyPaid() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setUsuarioId(UUID.randomUUID());
        dto.setTicketId(UUID.randomUUID());
        dto.setMonto(BigDecimal.TEN);

        User user = new User();
        Ticket ticket = new Ticket();
        ticket.setEstado(TicketState.PAGADO);

        when(userRepository.findById(dto.getUsuarioId())).thenReturn(Optional.of(user));
        when(ticketRepository.findById(dto.getTicketId())).thenReturn(Optional.of(ticket));

        assertThrows(ValidationException.class, () -> paymentService.createPayment(dto));
    }

    @Test
    void createPaymentShouldThrowWhenAmountInvalid() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setUsuarioId(UUID.randomUUID());
        dto.setTicketId(UUID.randomUUID());
        dto.setMonto(BigDecimal.ZERO);

        User user = new User();
        Ticket ticket = new Ticket();
        ticket.setEstado(TicketState.PENDIENTE_PAGO);

        when(userRepository.findById(dto.getUsuarioId())).thenReturn(Optional.of(user));
        when(ticketRepository.findById(dto.getTicketId())).thenReturn(Optional.of(ticket));

        assertThrows(ValidationException.class, () -> paymentService.createPayment(dto));
    }

    @Test
    void createPaymentShouldThrowWhenAmountDoesNotMatchTicket() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setUsuarioId(UUID.randomUUID());
        dto.setTicketId(UUID.randomUUID());
        dto.setMonto(BigDecimal.ONE);

        User user = new User();
        Ticket ticket = new Ticket();
        ticket.setEstado(TicketState.PENDIENTE_PAGO);
        ticket.setPrecio(BigDecimal.TEN);

        when(userRepository.findById(dto.getUsuarioId())).thenReturn(Optional.of(user));
        when(ticketRepository.findById(dto.getTicketId())).thenReturn(Optional.of(ticket));

        assertThrows(ValidationException.class, () -> paymentService.createPayment(dto));
    }

    @Test
    void getPaymentByIdShouldReturnPayment() {
        UUID paymentId = UUID.randomUUID();
        Payment payment = new Payment();
        PaymentResponseDTO dto = new PaymentResponseDTO();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(dto);

        PaymentResponseDTO result = paymentService.getPaymentById(paymentId);

        assertNotNull(result);
        verify(paymentRepository).findById(paymentId);
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void getPaymentByIdShouldThrowWhenNotFound() {
        UUID paymentId = UUID.randomUUID();
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.getPaymentById(paymentId));
    }

    @Test
    void getPaymentsByUserIdShouldReturnPage() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = Pageable.unpaged();
        Payment payment = new Payment();
        PaymentResponseDTO dto = new PaymentResponseDTO();
        Page<Payment> page = new PageImpl<>(List.of(payment));

        when(userRepository.existsById(userId)).thenReturn(true);
        when(paymentRepository.findByUsuarioId(userId, pageable)).thenReturn(page);
        when(paymentMapper.toDto(payment)).thenReturn(dto);

        Page<PaymentResponseDTO> result = paymentService.getPaymentsByUserId(userId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository).existsById(userId);
        verify(paymentRepository).findByUsuarioId(userId, pageable);
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void getPaymentsByUserIdShouldThrowWhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = Pageable.unpaged();
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> paymentService.getPaymentsByUserId(userId, pageable));
    }

    @Test
    void getPaymentsByTicketIdShouldReturnPage() {
        UUID ticketId = UUID.randomUUID();
        Pageable pageable = Pageable.ofSize(10);
        Payment payment = new Payment();
        PaymentResponseDTO dto = new PaymentResponseDTO();
        List<Payment> payments = List.of(payment);

        when(ticketRepository.existsById(ticketId)).thenReturn(true);
        when(paymentRepository.findByTicketId(ticketId)).thenReturn(payments);
        when(paymentMapper.toDto(payment)).thenReturn(dto);

        Page<PaymentResponseDTO> result = paymentService.getPaymentsByTicketId(ticketId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(ticketRepository).existsById(ticketId);
        verify(paymentRepository).findByTicketId(ticketId);
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void getPaymentsByTicketIdShouldThrowWhenTicketNotFound() {
        UUID ticketId = UUID.randomUUID();
        Pageable pageable = Pageable.unpaged();
        when(ticketRepository.existsById(ticketId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> paymentService.getPaymentsByTicketId(ticketId, pageable));
    }

    @Test
    void processPaymentShouldProcessWhenValid() {
        UUID paymentId = UUID.randomUUID();
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setEstado(EstadoPago.PENDIENTE);

        Ticket ticket = new Ticket();
        ticket.setEstado(TicketState.PENDIENTE_PAGO);
        Zone zone = new Zone();
        zone.setCapacidad(10);
        zone.setTickets(new ArrayList<>());
        Event event = new Event();
        event.setZonas(List.of(zone));
        zone.setEvento(event);
        ticket.setZona(zone);
        payment.setTicket(ticket);

        PaymentResponseDTO dto = new PaymentResponseDTO();

        when(paymentRepository.findByIdWithTicketAndZone(paymentId)).thenReturn(Optional.of(payment));
        when(ticketRepository.save(ticket)).thenReturn(ticket);
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(dto);

        PaymentResponseDTO result = paymentService.processPayment(paymentId);

        assertNotNull(result);
        assertEquals(EstadoPago.COMPLETADO, payment.getEstado());
        assertEquals(TicketState.PAGADO, ticket.getEstado());
        verify(paymentRepository).findByIdWithTicketAndZone(paymentId);
        verify(ticketRepository).save(ticket);
        verify(paymentRepository).save(payment);
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void processPaymentShouldThrowWhenAlreadyProcessed() {
        UUID paymentId = UUID.randomUUID();
        Payment payment = new Payment();
        payment.setEstado(EstadoPago.COMPLETADO);

        when(paymentRepository.findByIdWithTicketAndZone(paymentId)).thenReturn(Optional.of(payment));

        assertThrows(ValidationException.class, () -> paymentService.processPayment(paymentId));
    }

    @Test
    void refundPaymentShouldRefundWhenValid() {
        UUID paymentId = UUID.randomUUID();
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setEstado(EstadoPago.COMPLETADO);

        Ticket ticket = new Ticket();
        ticket.setEstado(TicketState.PAGADO);
        payment.setTicket(ticket);

        PaymentResponseDTO dto = new PaymentResponseDTO();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(ticketRepository.save(ticket)).thenReturn(ticket);
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(dto);

        PaymentResponseDTO result = paymentService.refundPayment(paymentId, "Motivo");

        assertNotNull(result);
        assertEquals(EstadoPago.REEMBOLSADO, payment.getEstado());
        assertEquals(TicketState.CANCELADO, ticket.getEstado());
        verify(paymentRepository).findById(paymentId);
        verify(ticketRepository).save(ticket);
        verify(paymentRepository).save(payment);
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void refundPaymentShouldThrowWhenNotCompleted() {
        UUID paymentId = UUID.randomUUID();
        Payment payment = new Payment();
        payment.setEstado(EstadoPago.PENDIENTE);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        assertThrows(ValidationException.class, () -> paymentService.refundPayment(paymentId, "Motivo"));
    }

    @Test
    void refundPaymentShouldThrowWhenNotFound() {
        UUID paymentId = UUID.randomUUID();
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.refundPayment(paymentId, "Motivo"));
    }
}