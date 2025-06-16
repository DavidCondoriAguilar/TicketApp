package com.tickets.ravetix.service.interfac;

import com.tickets.ravetix.dto.zone.ZoneCreateDTO;
import com.tickets.ravetix.dto.zone.ZoneResponseDTO;
import com.tickets.ravetix.dto.zone.ZoneUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ZoneService {
    /**
     * Obtiene todas las zonas de un evento específico con paginación
     * @param eventId ID del evento
     * @param pageable Configuración de paginación
     * @return Página de zonas
     */
    Page<ZoneResponseDTO> findAllByEventId(UUID eventId, Pageable pageable);

    /**
     * Obtiene una zona por su ID
     */
    ZoneResponseDTO findById(UUID id);

    /**
     * Crea una nueva zona para un evento
     */
    ZoneResponseDTO create(ZoneCreateDTO zoneDTO);

    /**
     * Actualiza una zona existente
     */
    ZoneResponseDTO update(UUID id, ZoneUpdateDTO zoneDTO);

    /**
     * Elimina una zona por su ID
     */
    void delete(UUID id);

    /**
     * Obtiene las zonas disponibles (con capacidad restante) de un evento con paginación
     * @param eventId ID del evento
     * @param pageable Configuración de paginación
     * @return Página de zonas disponibles
     */
    Page<ZoneResponseDTO> findAvailableZonesByEventId(UUID eventId, Pageable pageable);
}