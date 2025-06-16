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

    @PostMapping
    public ResponseEntity<TicketResponseDTO> createTicket(@Valid @RequestBody TicketRequestDTO ticketDTO) {
        TicketResponseDTO createdTicket = ticketService.createTicket(ticketDTO);
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> getTicketById(@PathVariable UUID id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<TicketResponseDTO>> getTicketsByUser(
            @PathVariable UUID userId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ticketService.getTicketsByUserId(userId, pageable));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<Page<TicketResponseDTO>> getTicketsByEvent(
            @PathVariable UUID eventId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ticketService.getTicketsByEventId(eventId, pageable));
    }

    @PostMapping("/{ticketId}/cancel")
    public ResponseEntity<Void> cancelTicket(
            @PathVariable UUID ticketId,
            @RequestParam(required = false) String reason) {
        ticketService.cancelTicket(ticketId, reason);
        return ResponseEntity.noContent().build();
    }
}
