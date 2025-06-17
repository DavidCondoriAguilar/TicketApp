package com.tickets.ravetix.controller;

import com.tickets.ravetix.dto.ticket.TicketRequestDTO;
import com.tickets.ravetix.dto.ticket.TicketResponseDTO;
import com.tickets.ravetix.service.interfac.TicketService;
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
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    /**
     * Crea un nuevo ticket para un usuario en una zona de un evento.
     *
     * @param ticketDTO Datos del ticket a crear.
     * @return Ticket creado.
     */
    @PostMapping
    public ResponseEntity<TicketResponseDTO> createTicket(@Valid @RequestBody TicketRequestDTO ticketDTO) {
        TicketResponseDTO createdTicket = ticketService.createTicket(ticketDTO);
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }

    /**
     * Obtiene un ticket por su ID.
     *
     * @param id ID del ticket.
     * @return Ticket encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> getTicketById(@PathVariable UUID id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    /**
     * Obtiene todos los tickets de un usuario, paginados.
     *
     * @param userId ID del usuario.
     * @param pageable Parámetros de paginación.
     * @return Página de tickets encontrados.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<TicketResponseDTO>> getTicketsByUser(
            @PathVariable UUID userId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ticketService.getTicketsByUserId(userId, pageable));
    }

    /**
     * Obtiene todos los tickets de un evento, paginados.
     *
     * @param eventId ID del evento.
     * @param pageable Parámetros de paginación.
     * @return Página de tickets encontrados.
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<Page<TicketResponseDTO>> getTicketsByEvent(
            @PathVariable UUID eventId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ticketService.getTicketsByEventId(eventId, pageable));
    }

    /**
     * Cancela un ticket existente.
     *
     * @param ticketId ID del ticket a cancelar.
     * @param reason Motivo de la cancelación (opcional).
     */
    @PostMapping("/{ticketId}/cancel")
    public ResponseEntity<Void> cancelTicket(
            @PathVariable UUID ticketId,
            @RequestParam(required = false) String reason) {
        ticketService.cancelTicket(ticketId, reason);
        return ResponseEntity.noContent().build();
    }
}
