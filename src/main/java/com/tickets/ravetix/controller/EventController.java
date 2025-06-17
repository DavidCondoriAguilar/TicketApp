package com.tickets.ravetix.controller;

import com.tickets.ravetix.dto.event.EventCreateDTO;
import com.tickets.ravetix.dto.event.EventResponseDTO;
import com.tickets.ravetix.dto.event.EventUpdateDTO;
import com.tickets.ravetix.entity.Event;
import com.tickets.ravetix.enums.EstadoEvento;
import com.tickets.ravetix.service.interfac.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    /**
     * Obtiene todos los eventos paginados.
     *
     * @param pageable Parámetros de paginación.
     * @return Página de eventos.
     */
    @GetMapping
    public ResponseEntity<Page<EventResponseDTO>> getAllEvents(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(eventService.findAll(pageable));
    }

    /**
     * Obtiene un evento por su ID.
     *
     * @param id ID del evento.
     * @return Evento encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable UUID id) {
        return ResponseEntity.ok(eventService.findById(id));
    }

    /**
     * Crea un nuevo evento.
     *
     * @param eventCreateDTO Datos del evento a crear.
     * @return Evento creado.
     */
    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(@Valid @RequestBody EventCreateDTO eventCreateDTO) {
        return new ResponseEntity<>(eventService.createFromDto(eventCreateDTO), HttpStatus.CREATED);
    }

    /**
     * Actualiza un evento existente.
     *
     * @param id ID del evento.
     * @param eventUpdateDTO Datos actualizados del evento.
     * @return Evento actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDTO> updateEvent(
            @PathVariable UUID id,
            @Valid @RequestBody EventUpdateDTO eventUpdateDTO) {
        return ResponseEntity.ok(eventService.update(id, eventUpdateDTO));
    }

    /**
     * Elimina un evento por su ID.
     *
     * @param id ID del evento.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene eventos filtrados por estado.
     *
     * @param status Estado del evento.
     * @param pageable Parámetros de paginación.
     * @return Página de eventos filtrados.
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<EventResponseDTO>> getEventsByStatus(
            @PathVariable EstadoEvento status,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(eventService.findByEstado(status, pageable));
    }

    /**
     * Busca eventos por texto.
     *
     * @param query Texto de búsqueda.
     * @param pageable Parámetros de paginación.
     * @return Página de eventos encontrados.
     */
    @GetMapping("/search")
    public ResponseEntity<Page<EventResponseDTO>> searchEvents(
            @RequestParam String query,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(eventService.search(query, pageable));
    }

    /**
     * Obtiene eventos entre dos fechas.
     *
     * @param startDate Fecha de inicio.
     * @param endDate Fecha de fin.
     * @param pageable Parámetros de paginación.
     * @return Página de eventos encontrados.
     */
    @GetMapping("/date-range")
    public ResponseEntity<Page<EventResponseDTO>> getEventsBetweenDates(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(eventService.findBetweenDates(startDate, endDate, pageable));
    }

    /**
     * Cambia el estado de un evento.
     *
     * @param id ID del evento.
     * @param status Nuevo estado.
     * @return Evento actualizado.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<EventResponseDTO> updateEventStatus(
            @PathVariable UUID id,
            @RequestParam EstadoEvento status) {
        return ResponseEntity.ok(eventService.changeStatus(id, status));
    }
}
