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

    @GetMapping
    public ResponseEntity<Page<EventResponseDTO>> getAllEvents(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(eventService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable UUID id) {
        return ResponseEntity.ok(eventService.findById(id));
    }

    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(@Valid @RequestBody EventCreateDTO eventCreateDTO) {
        return new ResponseEntity<>(eventService.createFromDto(eventCreateDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDTO> updateEvent(
            @PathVariable UUID id,
            @Valid @RequestBody EventUpdateDTO eventUpdateDTO) {
        return ResponseEntity.ok(eventService.update(id, eventUpdateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<EventResponseDTO>> getEventsByStatus(
            @PathVariable EstadoEvento status,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(eventService.findByEstado(status, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<EventResponseDTO>> searchEvents(
            @RequestParam String query,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(eventService.search(query, pageable));
    }

    @GetMapping("/date-range")
    public ResponseEntity<Page<EventResponseDTO>> getEventsBetweenDates(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(eventService.findBetweenDates(startDate, endDate, pageable));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<EventResponseDTO> updateEventStatus(
            @PathVariable UUID id,
            @RequestParam EstadoEvento status) {
        return ResponseEntity.ok(eventService.changeStatus(id, status));
    }
}
