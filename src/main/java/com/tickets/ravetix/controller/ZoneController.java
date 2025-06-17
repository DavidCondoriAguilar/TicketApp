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

    /**
     * Obtiene todas las zonas asociadas a un evento, paginadas.
     *
     * @param eventId ID del evento.
     * @param pageable Parámetros de paginación.
     * @return Página de zonas del evento.
     */
    @GetMapping
    public ResponseEntity<Page<ZoneResponseDTO>> getAllZonesByEventId(
            @PathVariable UUID eventId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(zoneService.findAllByEventId(eventId, pageable));
    }

    /**
     * Obtiene todas las zonas disponibles (con capacidad) para un evento, paginadas.
     *
     * @param eventId ID del evento.
     * @param pageable Parámetros de paginación.
     * @return Página de zonas disponibles.
     */
    @GetMapping("/available")
    public ResponseEntity<Page<ZoneResponseDTO>> getAvailableZonesByEventId(
            @PathVariable UUID eventId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(zoneService.findAvailableZonesByEventId(eventId, pageable));
    }

    /**
     * Obtiene una zona específica por su ID dentro de un evento.
     *
     * @param eventId ID del evento.
     * @param zoneId ID de la zona.
     * @return Zona encontrada.
     */
    @GetMapping("/{zoneId}")
    public ResponseEntity<ZoneResponseDTO> getZoneById(
            @PathVariable UUID eventId,
            @PathVariable UUID zoneId) {
        return ResponseEntity.ok(zoneService.findById(zoneId));
    }

    /**
     * Crea una nueva zona para un evento.
     *
     * @param eventId ID del evento.
     * @param zoneCreateDTO Datos de la zona a crear.
     * @return Zona creada.
     */
    @PostMapping
    public ResponseEntity<ZoneResponseDTO> createZone(
            @PathVariable UUID eventId,
            @Valid @RequestBody ZoneCreateDTO zoneCreateDTO) {
        zoneCreateDTO.setEventoId(eventId);
        ZoneResponseDTO createdZone = zoneService.create(zoneCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdZone);
    }

    /**
     * Actualiza una zona existente de un evento.
     *
     * @param eventId ID del evento.
     * @param zoneId ID de la zona.
     * @param zoneUpdateDTO Datos actualizados de la zona.
     * @return Zona actualizada.
     */
    @PutMapping("/{zoneId}")
    public ResponseEntity<ZoneResponseDTO> updateZone(
            @PathVariable UUID eventId,
            @PathVariable UUID zoneId,
            @Valid @RequestBody ZoneUpdateDTO zoneUpdateDTO) {
        zoneUpdateDTO.setEventoId(eventId);
        return ResponseEntity.ok(zoneService.update(zoneId, zoneUpdateDTO));
    }

    /**
     * Elimina una zona de un evento por su ID.
     *
     * @param eventId ID del evento.
     * @param zoneId ID de la zona.
     */
    @DeleteMapping("/{zoneId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteZone(
            @PathVariable UUID eventId,
            @PathVariable UUID zoneId) {
        zoneService.delete(zoneId);
    }
}
