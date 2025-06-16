package com.tickets.ravetix.service.interfac;

import com.tickets.ravetix.dto.ticket.TicketRequestDTO;
import com.tickets.ravetix.dto.ticket.TicketResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TicketService {
    /**
     * Crea un nuevo ticket para un evento
     * @param ticketDTO Datos del ticket a crear
     * @return Ticket creado
     */
    TicketResponseDTO createTicket(TicketRequestDTO ticketDTO);

    /**
     * Obtiene un ticket por su ID
     * @param id ID del ticket
     * @return Ticket encontrado
     */
    TicketResponseDTO getTicketById(UUID id);

    /**
     * Obtiene todos los tickets de un usuario
     * @param userId ID del usuario
     * @param pageable Configuración de paginación
     * @return Página de tickets del usuario
     */
    Page<TicketResponseDTO> getTicketsByUserId(UUID userId, Pageable pageable);

    /**
     * Obtiene todos los tickets de un evento
     * @param eventId ID del evento
     * @param pageable Configuración de paginación
     * @return Página de tickets del evento
     */
    Page<TicketResponseDTO> getTicketsByEventId(UUID eventId, Pageable pageable);

    /**
     * Cancela un ticket
     * @param ticketId ID del ticket a cancelar
     * @param reason Motivo de la cancelación
     */
    void cancelTicket(UUID ticketId, String reason);
}
