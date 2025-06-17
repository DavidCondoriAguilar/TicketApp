package com.tickets.ravetix.service.impl;

import com.tickets.ravetix.dto.mapper.ZoneMapper;
import com.tickets.ravetix.dto.zone.ZoneCreateDTO;
import com.tickets.ravetix.dto.zone.ZoneResponseDTO;
import com.tickets.ravetix.dto.zone.ZoneUpdateDTO;
import com.tickets.ravetix.entity.Event;
import com.tickets.ravetix.entity.Zone;
import com.tickets.ravetix.exception.NotFoundException;
import com.tickets.ravetix.exception.ValidationException;
import com.tickets.ravetix.repository.EventRepository;
import com.tickets.ravetix.repository.ZoneRepository;
import com.tickets.ravetix.service.interfac.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of the {@link ZoneService} interface for managing zones within events.
 * <p>
 * This service provides methods to create, update, delete, and retrieve zones associated with events.
 * It ensures business rules such as unique zone names per event and prevents deletion of zones with associated tickets.
 * </p>
 *
 * <p>
 * Main responsibilities:
 * <ul>
 *     <li>Find all zones by event ID with pagination support.</li>
 *     <li>Retrieve a specific zone by its ID.</li>
 *     <li>Create a new zone for a given event, ensuring unique zone names within the event.</li>
 *     <li>Update an existing zone, enforcing name uniqueness constraints.</li>
 *     <li>Delete a zone only if it has no associated tickets.</li>
 *     <li>Find available zones for an event with pagination support.</li>
 * </ul>
 * </p>
 *
 * <p>
 * This class uses {@link ZoneRepository}, {@link EventRepository}, and {@link ZoneMapper} for data access and mapping.
 * Transactional boundaries are defined for each method as appropriate.
 * </p>
 *
 * @author david
 */
@Service
@RequiredArgsConstructor
public class ZoneServiceImpl implements ZoneService {
    private final ZoneRepository zoneRepository;
    private final EventRepository eventRepository;
    private final ZoneMapper zoneMapper;

    /**
     * Retrieves a paginated list of all zones for a specific event.
     *
     * @param eventId  The ID of the event.
     * @param pageable Pagination information.
     * @return A paginated list of zones.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ZoneResponseDTO> findAllByEventId(UUID eventId, Pageable pageable) {
        return zoneRepository.findByEventoId(eventId, pageable)
                .map(zoneMapper::toDto);
    }

    /**
     * Retrieves a specific zone by its ID.
     *
     * @param id The ID of the zone.
     * @return The zone details.
     * @throws NotFoundException if the zone is not found.
     */
    @Override
    @Transactional(readOnly = true)
    public ZoneResponseDTO findById(UUID id) {
        return zoneRepository.findById(id)
                .map(zoneMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Zona no encontrada con ID: " + id));
    }

    /**
     * Creates a new zone for a given event.
     *
     * @param zoneDTO The zone data for creation.
     * @return The created zone details.
     * @throws NotFoundException   if the event is not found.
     * @throws ValidationException if a zone with the same name already exists for the event.
     */
    @Override
    @Transactional
    public ZoneResponseDTO create(ZoneCreateDTO zoneDTO) {
        Event event = eventRepository.findById(zoneDTO.getEventoId())
                .orElseThrow(() -> new NotFoundException("Evento no encontrado con ID: " + zoneDTO.getEventoId()));

        if (zoneRepository.existsByEventoIdAndNombre(zoneDTO.getEventoId(), zoneDTO.getNombre())) {
            throw new ValidationException("Validación fallida", "Ya existe una zona con el mismo nombre en este evento");
        }

        Zone zone = zoneMapper.toEntity(zoneDTO);
        zone.setEvento(event);
        Zone savedZone = zoneRepository.save(zone);
        return zoneMapper.toDto(savedZone);
    }

    /**
     * Updates an existing zone.
     *
     * @param id      The ID of the zone to update.
     * @param zoneDTO The new zone data.
     * @return The updated zone details.
     * @throws NotFoundException   if the zone is not found.
     * @throws ValidationException if a zone with the same name already exists for the event.
     */
    @Override
    @Transactional
    public ZoneResponseDTO update(UUID id, ZoneUpdateDTO zoneDTO) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Zona no encontrada con ID: " + id));

        if (zoneRepository.existsByEventoIdAndNombreAndIdNot(
                zone.getEvento().getId(),
                zoneDTO.getNombre(),
                id)) {
            throw new ValidationException("Validación fallida", "Ya existe una zona con el mismo nombre en este evento");
        }

        zoneMapper.updateZoneFromDto(zoneDTO, zone);
        Zone updatedZone = zoneRepository.save(zone);
        return zoneMapper.toDto(updatedZone);
    }

    /**
     * Deletes a zone by its ID.
     *
     * @param id The ID of the zone to delete.
     * @throws NotFoundException   if the zone is not found.
     * @throws ValidationException if the zone has associated tickets.
     */
    @Override
    @Transactional
    public void delete(UUID id) {
        Zone zone = zoneRepository.findByIdWithTickets(id)
                .orElseThrow(() -> new NotFoundException("Zona no encontrada con ID: " + id));

        if (!zone.getTickets().isEmpty()) {
            throw new ValidationException("Operación no permitida", "No se puede eliminar la zona porque tiene tickets asociados");
        }

        zoneRepository.delete(zone);
    }

    /**
     * Retrieves a paginated list of available zones for a specific event.
     *
     * @param eventId  The ID of the event.
     * @param pageable Pagination information.
     * @return A paginated list of available zones.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ZoneResponseDTO> findAvailableZonesByEventId(UUID eventId, Pageable pageable) {
        return zoneRepository.findAvailableZonesByEventId(eventId, pageable)
                .map(zoneMapper::toDto);
    }
}