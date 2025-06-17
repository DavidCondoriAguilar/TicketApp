package com.tickets.ravetix.service;

import com.tickets.ravetix.dto.mapper.TicketMapper;
import com.tickets.ravetix.dto.ticket.TicketRequestDTO;
import com.tickets.ravetix.dto.ticket.TicketResponseDTO;
import com.tickets.ravetix.entity.*;
import com.tickets.ravetix.enums.TicketState;
import com.tickets.ravetix.exception.NotFoundException;
import com.tickets.ravetix.exception.ValidationException;
import com.tickets.ravetix.repository.EventRepository;
import com.tickets.ravetix.repository.TicketRepository;
import com.tickets.ravetix.repository.UserRepository;
import com.tickets.ravetix.repository.ZoneRepository;
import com.tickets.ravetix.service.impl.TicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private ZoneRepository zoneRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TicketMapper ticketMapper;

    @InjectMocks
    private TicketServiceImpl ticketService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTicketShouldCreateTicketWhenAllValidationsPass() {
        UUID eventId = UUID.randomUUID();
        UUID zoneId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        TicketRequestDTO dto = new TicketRequestDTO();
        dto.setEventoId(eventId);
        dto.setZonaId(zoneId);
        dto.setCompradorId(userId);
        dto.setPrecio(BigDecimal.TEN);

        Event event = new Event();
        event.setId(eventId);

        Zone zone = new Zone();
        zone.setId(zoneId);
        zone.setEvento(event);
        zone.setCapacidad(10);
        zone.setTickets(new ArrayList<>());
        zone.setPrecioBase(BigDecimal.TEN);

        User user = new User();
        user.setId(userId);

        Ticket ticket = new Ticket();
        Ticket savedTicket = new Ticket();
        TicketResponseDTO responseDTO = new TicketResponseDTO();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(zoneRepository.findById(zoneId)).thenReturn(Optional.of(zone));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);
        when(ticketMapper.toDto(savedTicket)).thenReturn(responseDTO);

        TicketResponseDTO result = ticketService.createTicket(dto);

        assertNotNull(result);
        verify(eventRepository).findById(eventId);
        verify(zoneRepository).findById(zoneId);
        verify(userRepository).findById(userId);
        verify(ticketRepository).save(any(Ticket.class));
        verify(ticketMapper).toDto(savedTicket);
    }

    @Test
    void createTicketShouldThrowWhenEventNotFound() {
        UUID eventId = UUID.randomUUID();
        TicketRequestDTO dto = new TicketRequestDTO();
        dto.setEventoId(eventId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> ticketService.createTicket(dto));
        verify(eventRepository).findById(eventId);
    }

    @Test
    void createTicketShouldThrowWhenZoneNotFound() {
        UUID eventId = UUID.randomUUID();
        UUID zoneId = UUID.randomUUID();
        TicketRequestDTO dto = new TicketRequestDTO();
        dto.setEventoId(eventId);
        dto.setZonaId(zoneId);

        Event event = new Event();
        event.setId(eventId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(zoneRepository.findById(zoneId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> ticketService.createTicket(dto));
        verify(zoneRepository).findById(zoneId);
    }

    @Test
    void createTicketShouldThrowWhenUserNotFound() {
        UUID eventId = UUID.randomUUID();
        UUID zoneId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        TicketRequestDTO dto = new TicketRequestDTO();
        dto.setEventoId(eventId);
        dto.setZonaId(zoneId);
        dto.setCompradorId(userId);

        Event event = new Event();
        event.setId(eventId);

        Zone zone = new Zone();
        zone.setId(zoneId);
        zone.setEvento(event);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(zoneRepository.findById(zoneId)).thenReturn(Optional.of(zone));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> ticketService.createTicket(dto));
        verify(userRepository).findById(userId);
    }

    @Test
    void createTicketShouldThrowWhenZoneDoesNotBelongToEvent() {
        UUID eventId = UUID.randomUUID();
        UUID zoneId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        TicketRequestDTO dto = new TicketRequestDTO();
        dto.setEventoId(eventId);
        dto.setZonaId(zoneId);
        dto.setCompradorId(userId);

        Event event = new Event();
        event.setId(eventId);

        Event otherEvent = new Event();
        otherEvent.setId(UUID.randomUUID());

        Zone zone = new Zone();
        zone.setId(zoneId);
        zone.setEvento(otherEvent);

        User user = new User();
        user.setId(userId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(zoneRepository.findById(zoneId)).thenReturn(Optional.of(zone));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(ValidationException.class, () -> ticketService.createTicket(dto));
    }

    @Test
    void createTicketShouldThrowWhenZoneIsFull() {
        UUID eventId = UUID.randomUUID();
        UUID zoneId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        TicketRequestDTO dto = new TicketRequestDTO();
        dto.setEventoId(eventId);
        dto.setZonaId(zoneId);
        dto.setCompradorId(userId);

        Event event = new Event();
        event.setId(eventId);

        Zone zone = new Zone();
        zone.setId(zoneId);
        zone.setEvento(event);
        zone.setCapacidad(1);
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(new Ticket());
        zone.setTickets(tickets);

        User user = new User();
        user.setId(userId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(zoneRepository.findById(zoneId)).thenReturn(Optional.of(zone));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(ValidationException.class, () -> ticketService.createTicket(dto));
    }

    @Test
    void getTicketByIdShouldReturnTicket() {
        UUID ticketId = UUID.randomUUID();
        Ticket ticket = new Ticket();
        TicketResponseDTO dto = new TicketResponseDTO();

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(ticketMapper.toDto(ticket)).thenReturn(dto);

        TicketResponseDTO result = ticketService.getTicketById(ticketId);

        assertNotNull(result);
        verify(ticketRepository).findById(ticketId);
        verify(ticketMapper).toDto(ticket);
    }

    @Test
    void getTicketByIdShouldThrowWhenNotFound() {
        UUID ticketId = UUID.randomUUID();
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> ticketService.getTicketById(ticketId));
        verify(ticketRepository).findById(ticketId);
    }

    @Test
    void getTicketsByUserIdShouldReturnPage() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = Pageable.unpaged();
        Ticket ticket = new Ticket();
        TicketResponseDTO dto = new TicketResponseDTO();
        Page<Ticket> page = new PageImpl<>(List.of(ticket));

        when(userRepository.existsById(userId)).thenReturn(true);
        when(ticketRepository.findByUsuarioId(userId, pageable)).thenReturn(page);
        when(ticketMapper.toDto(ticket)).thenReturn(dto);

        Page<TicketResponseDTO> result = ticketService.getTicketsByUserId(userId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository).existsById(userId);
        verify(ticketRepository).findByUsuarioId(userId, pageable);
        verify(ticketMapper).toDto(ticket);
    }

    @Test
    void getTicketsByUserIdShouldThrowWhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = Pageable.unpaged();
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> ticketService.getTicketsByUserId(userId, pageable));
        verify(userRepository).existsById(userId);
    }

    @Test
    void getTicketsByEventIdShouldReturnPage() {
        UUID eventId = UUID.randomUUID();
        Pageable pageable = Pageable.unpaged();
        Ticket ticket = new Ticket();
        TicketResponseDTO dto = new TicketResponseDTO();
        Page<Ticket> page = new PageImpl<>(List.of(ticket));

        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(ticketRepository.findByEventoId(eventId, pageable)).thenReturn(page);
        when(ticketMapper.toDto(ticket)).thenReturn(dto);

        Page<TicketResponseDTO> result = ticketService.getTicketsByEventId(eventId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(eventRepository).existsById(eventId);
        verify(ticketRepository).findByEventoId(eventId, pageable);
        verify(ticketMapper).toDto(ticket);
    }

    @Test
    void getTicketsByEventIdShouldThrowWhenEventNotFound() {
        UUID eventId = UUID.randomUUID();
        Pageable pageable = Pageable.unpaged();
        when(eventRepository.existsById(eventId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> ticketService.getTicketsByEventId(eventId, pageable));
        verify(eventRepository).existsById(eventId);
    }

    @Test
    void cancelTicketShouldCancelWhenValid() {
        UUID ticketId = UUID.randomUUID();
        Ticket ticket = new Ticket();
        ticket.setEstado(TicketState.PENDIENTE_PAGO);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        assertDoesNotThrow(() -> ticketService.cancelTicket(ticketId, "Motivo"));
        assertEquals(TicketState.CANCELADO, ticket.getEstado());
        assertEquals("Motivo", ticket.getMotivoCancelacion());
        verify(ticketRepository).findById(ticketId);
        verify(ticketRepository).save(ticket);
    }

    @Test
    void cancelTicketShouldThrowWhenAlreadyCancelled() {
        UUID ticketId = UUID.randomUUID();
        Ticket ticket = new Ticket();
        ticket.setEstado(TicketState.CANCELADO);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        assertThrows(ValidationException.class, () -> ticketService.cancelTicket(ticketId, "Motivo"));
        verify(ticketRepository).findById(ticketId);
    }

    @Test
    void cancelTicketShouldThrowWhenUsed() {
        UUID ticketId = UUID.randomUUID();
        Ticket ticket = new Ticket();
        ticket.setEstado(TicketState.USADO);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        assertThrows(ValidationException.class, () -> ticketService.cancelTicket(ticketId, "Motivo"));
        verify(ticketRepository).findById(ticketId);
    }

    @Test
    void cancelTicketShouldThrowWhenNotFound() {
        UUID ticketId = UUID.randomUUID();
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> ticketService.cancelTicket(ticketId, "Motivo"));
        verify(ticketRepository).findById(ticketId);
    }
}