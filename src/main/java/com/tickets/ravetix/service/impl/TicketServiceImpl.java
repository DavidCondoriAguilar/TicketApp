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
import com.tickets.ravetix.util.EventStatisticsCalculator;

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

    /**
     * Crea un nuevo ticket para un usuario en una zona específica de un evento, validando la existencia de las entidades
     * relacionadas y la disponibilidad de la zona. Si la zona no pertenece al evento o no hay capacidad, lanza una excepción.
     *
     * @param ticketDTO Objeto de transferencia con los datos necesarios para crear el ticket.
     * @return TicketResponseDTO con la información del ticket creado.
     * @throws NotFoundException si el evento, zona o usuario no existen.
     * @throws ValidationException si la zona no pertenece al evento o no hay entradas disponibles.
     */
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
        
        // Actualizar estadísticas del evento y zona
        EventStatisticsCalculator.calculateEventStatistics(event);
        EventStatisticsCalculator.calculateZoneStatistics(zone);
        
        return ticketMapper.toDto(savedTicket);
    }
    
    /**
     * Genera un código QR único para el ticket. Método auxiliar que puede ser implementado según los requisitos del sistema.
     *
     * @return String con el código QR generado.
     */
    private String generateQrCode() {
        // Implementa la generación del código QR aquí
        return "QR-" + UUID.randomUUID().toString();
    }

    /**
     * Recupera la información de un ticket por su identificador único (UUID).
     *
     * @param id Identificador único del ticket.
     * @return TicketResponseDTO con los datos del ticket encontrado.
     * @throws NotFoundException si el ticket no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public TicketResponseDTO getTicketById(UUID id) {
        return ticketRepository.findById(id)
                .map(ticketMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Ticket no encontrado con ID: " + id));
    }

    /**
     * Obtiene una lista paginada de tickets asociados a un usuario específico.
     *
     * @param userId Identificador único del usuario.
     * @param pageable Parámetro de paginación y ordenamiento.
     * @return Page<TicketResponseDTO> página de tickets encontrados.
     * @throws NotFoundException si el usuario no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TicketResponseDTO> getTicketsByUserId(UUID userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Usuario no encontrado con ID: " + userId);
        }
        return ticketRepository.findByUsuarioId(userId, pageable)
                .map(ticketMapper::toDto);
    }

    /**
     * Obtiene una lista paginada de tickets asociados a un evento específico.
     *
     * @param eventId Identificador único del evento.
     * @param pageable Parámetro de paginación y ordenamiento.
     * @return Page<TicketResponseDTO> página de tickets encontrados.
     * @throws NotFoundException si el evento no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TicketResponseDTO> getTicketsByEventId(UUID eventId, Pageable pageable) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Evento no encontrado con ID: " + eventId);
        }
        return ticketRepository.findByEventoId(eventId, pageable)
                .map(ticketMapper::toDto);
    }

    /**
     * Cancela un ticket existente, validando que no haya sido previamente cancelado o utilizado. Actualiza el estado y registra el motivo de cancelación.
     * Puede ser extendido para incluir lógica de reembolso si es necesario.
     *
     * @param ticketId Identificador único del ticket a cancelar.
     * @param reason Motivo de la cancelación.
     * @throws NotFoundException si el ticket no existe.
     * @throws ValidationException si el ticket ya está cancelado o ha sido utilizado.
     */
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
