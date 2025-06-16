package com.tickets.ravetix.controller;

import com.tickets.ravetix.dto.zone.ZoneCreateDTO;
import com.tickets.ravetix.dto.zone.ZoneResponseDTO;
import com.tickets.ravetix.dto.zone.ZoneUpdateDTO;
import com.tickets.ravetix.service.interfac.ZoneService;
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
@RequestMapping("/api/events/{eventId}/zones")
@RequiredArgsConstructor
public class ZoneController {
    private final ZoneService zoneService;

    @GetMapping
    public ResponseEntity<Page<ZoneResponseDTO>> getAllZonesByEventId(
            @PathVariable UUID eventId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(zoneService.findAllByEventId(eventId, pageable));
    }

    @GetMapping("/available")
    public ResponseEntity<Page<ZoneResponseDTO>> getAvailableZonesByEventId(
            @PathVariable UUID eventId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(zoneService.findAvailableZonesByEventId(eventId, pageable));
    }

    @GetMapping("/{zoneId}")
    public ResponseEntity<ZoneResponseDTO> getZoneById(
            @PathVariable UUID eventId,
            @PathVariable UUID zoneId) {
        return ResponseEntity.ok(zoneService.findById(zoneId));
    }

    @PostMapping
    public ResponseEntity<ZoneResponseDTO> createZone(
            @PathVariable UUID eventId,
            @Valid @RequestBody ZoneCreateDTO zoneCreateDTO) {
        zoneCreateDTO.setEventoId(eventId);
        ZoneResponseDTO createdZone = zoneService.create(zoneCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdZone);
    }

    @PutMapping("/{zoneId}")
    public ResponseEntity<ZoneResponseDTO> updateZone(
            @PathVariable UUID eventId,
            @PathVariable UUID zoneId,
            @Valid @RequestBody ZoneUpdateDTO zoneUpdateDTO) {
        zoneUpdateDTO.setEventoId(eventId);
        return ResponseEntity.ok(zoneService.update(zoneId, zoneUpdateDTO));
    }

    @DeleteMapping("/{zoneId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteZone(
            @PathVariable UUID eventId,
            @PathVariable UUID zoneId) {
        zoneService.delete(zoneId);
    }
}
