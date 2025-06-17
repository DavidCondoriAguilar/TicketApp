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

    @GetMapping("/{id}")
    public ResponseEntity<EventHistoryResponseDTO> getEventHistoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(eventHistoryService.getEventHistoryById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<EventHistoryResponseDTO>> getEventHistoryByUserId(
            @PathVariable UUID userId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(eventHistoryService.getEventHistoryByUserId(userId, pageable));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<Page<EventHistoryResponseDTO>> getEventHistoryByEventId(
            @PathVariable UUID eventId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(eventHistoryService.getEventHistoryByEventId(eventId, pageable));
    }

    @PostMapping("/{eventId}/confirm-attendance/{userId}")
    public ResponseEntity<EventHistoryResponseDTO> confirmAttendance(
            @PathVariable UUID eventId,
            @PathVariable UUID userId) {
        return ResponseEntity.ok(eventHistoryService.confirmAttendance(eventId, userId));
    }

    @PostMapping("/{eventId}/rate/{userId}")
    public ResponseEntity<EventHistoryResponseDTO> rateEvent(
            @PathVariable UUID eventId,
            @PathVariable UUID userId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment) {
        return ResponseEntity.ok(eventHistoryService.rateEvent(eventId, userId, rating, comment));
    }
}
