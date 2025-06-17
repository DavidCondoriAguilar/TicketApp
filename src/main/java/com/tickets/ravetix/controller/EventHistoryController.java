package com.tickets.ravetix.controller;

import com.tickets.ravetix.dto.eventhistory.EventHistoryResponseDTO;
import com.tickets.ravetix.service.interfac.EventHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/event-history")
@RequiredArgsConstructor
public class EventHistoryController {

    private final EventHistoryService eventHistoryService;

    /**
     * Obtiene el historial de eventos por su ID.
     *
     * @param id ID del historial de evento.
     * @return Historial encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventHistoryResponseDTO> getEventHistoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(eventHistoryService.getEventHistoryById(id));
    }

    /**
     * Obtiene el historial de eventos de un usuario, paginado.
     *
     * @param userId ID del usuario.
     * @param pageable Parámetros de paginación.
     * @return Página de historiales encontrados.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<EventHistoryResponseDTO>> getEventHistoryByUserId(
            @PathVariable UUID userId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(eventHistoryService.getEventHistoryByUserId(userId, pageable));
    }

    /**
     * Obtiene el historial de eventos de un evento, paginado.
     *
     * @param eventId ID del evento.
     * @param pageable Parámetros de paginación.
     * @return Página de historiales encontrados.
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<Page<EventHistoryResponseDTO>> getEventHistoryByEventId(
            @PathVariable UUID eventId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(eventHistoryService.getEventHistoryByEventId(eventId, pageable));
    }

    /**
     * Confirma la asistencia de un usuario a un evento.
     *
     * @param eventId ID del evento.
     * @param userId ID del usuario.
     * @return Historial actualizado.
     */
    @PostMapping("/{eventId}/confirm-attendance/{userId}")
    public ResponseEntity<EventHistoryResponseDTO> confirmAttendance(
            @PathVariable UUID eventId,
            @PathVariable UUID userId) {
        return ResponseEntity.ok(eventHistoryService.confirmAttendance(eventId, userId));
    }

    /**
     * Registra la calificación y comentario de un usuario para un evento.
     *
     * @param eventId ID del evento.
     * @param userId ID del usuario.
     * @param rating Calificación (1-5).
     * @param comment Comentario opcional.
     * @return Historial actualizado.
     */
    @PostMapping("/{eventId}/rate/{userId}")
    public ResponseEntity<EventHistoryResponseDTO> rateEvent(
            @PathVariable UUID eventId,
            @PathVariable UUID userId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment) {
        return ResponseEntity.ok(eventHistoryService.rateEvent(eventId, userId, rating, comment));
    }
}
