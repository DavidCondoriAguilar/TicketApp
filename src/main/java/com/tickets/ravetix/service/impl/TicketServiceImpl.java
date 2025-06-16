package com.tickets.ravetix.service.impl;

import com.tickets.ravetix.dto.mapper.TicketMapper;
import com.tickets.ravetix.dto.ticket.TicketRequestDTO;
import com.tickets.ravetix.dto.ticket.TicketResponseDTO;
import com.tickets.ravetix.enums.TicketState;
import com.tickets.ravetix.entity.*;
import com.tickets.ravetix.exception.NotFoundException;
import com.tickets.ravetix.exception.ValidationException;
import com.tickets.ravetix.repository.EventRepository;
import com.tickets.ravetix.repository.TicketRepository;
import com.tickets.ravetix.repository.UserRepository;
import com.tickets.ravetix.repository.ZoneRepository;
import com.tickets.ravetix.service.interfac.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final ZoneRepository zoneRepository;
    private final UserRepository userRepository;
    private final TicketMapper ticketMapper;

    @Override
    @Transactional
    public TicketResponseDTO createTicket(TicketRequestDTO ticketDTO) {
        // Buscar entidades relacionadas
        Event event = eventRepository.findById(ticketDTO.getEventoId())
                .orElseThrow(() -> new NotFoundException("Evento no encontrado con ID: " + ticketDTO.getEventoId()));

        Zone zone = zoneRepository.findById(ticketDTO.getZonaId())
                .orElseThrow(() -> new NotFoundException("Zona no encontrada con ID: " + ticketDTO.getZonaId()));

        // Validar que el usuario (comprador) existe
        User user = userRepository.findById(ticketDTO.getCompradorId())
                .orElseThrow(() -> new NotFoundException("Usuario comprador no encontrado con ID: " + ticketDTO.getCompradorId()));

        // Validar que la zona pertenece al evento
        if (!zone.getEvento().getId().equals(event.getId())) {
            throw new ValidationException("Validación fallida", 
                    "La zona no pertenece al evento especificado");
        }

        // Validar disponibilidad
        if (zone.getTickets().size() >= zone.getCapacidad()) {
            throw new ValidationException("Validación fallida", "No hay entradas disponibles para esta zona");
        }

        // Crear el ticket
        Ticket ticket = new Ticket();
        ticket.setEvento(event);
        ticket.setZona(zone);
        ticket.setUsuario(user);
        ticket.setPrecio(ticketDTO.getPrecio() != null ? ticketDTO.getPrecio() : zone.getPrecioBase());
        // El estado se establece automáticamente a PENDIENTE_PAGO por defecto en la entidad
        ticket.setFechaCreacion(LocalDateTime.now());
        ticket.setFechaActualizacion(LocalDateTime.now());

        // Guardar el ticket
        Ticket savedTicket = ticketRepository.save(ticket);
        
        return ticketMapper.toDto(savedTicket);
    }
    
    // Método auxiliar para generar códigos QR (puedes implementarlo según tus necesidades)
    private String generateQrCode() {
        // Implementa la generación del código QR aquí
        return "QR-" + UUID.randomUUID().toString();
    }

    @Override
    @Transactional(readOnly = true)
    public TicketResponseDTO getTicketById(UUID id) {
        return ticketRepository.findById(id)
                .map(ticketMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Ticket no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketResponseDTO> getTicketsByUserId(UUID userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Usuario no encontrado con ID: " + userId);
        }
        return ticketRepository.findByUsuarioId(userId, pageable)
                .map(ticketMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketResponseDTO> getTicketsByEventId(UUID eventId, Pageable pageable) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Evento no encontrado con ID: " + eventId);
        }
        return ticketRepository.findByEventoId(eventId, pageable)
                .map(ticketMapper::toDto);
    }

    @Override
    @Transactional
    public void cancelTicket(UUID ticketId, String reason) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("Ticket no encontrado con ID: " + ticketId));

        // Validar que el ticket se puede cancelar
        if (ticket.getEstado() == TicketState.CANCELADO) {
            throw new ValidationException("Validación fallida", "El ticket ya está cancelado");
        }

        if (ticket.getEstado() == TicketState.USADO) {
            throw new ValidationException("Validación fallida", "No se puede cancelar un ticket ya utilizado");
        }

        // Actualizar estado del ticket
        ticket.setEstado(TicketState.CANCELADO);
        // Agregar el motivo de cancelación
        ticket.setMotivoCancelacion(reason);
        ticket.setFechaActualizacion(LocalDateTime.now());

        ticketRepository.save(ticket);

        // Aquí podrías agregar lógica para reembolsos si es necesario
    }
}
