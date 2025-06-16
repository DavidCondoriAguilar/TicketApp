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

@Service
@RequiredArgsConstructor
public class ZoneServiceImpl implements ZoneService {
    private final ZoneRepository zoneRepository;
    private final EventRepository eventRepository;
    private final ZoneMapper zoneMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ZoneResponseDTO> findAllByEventId(UUID eventId, Pageable pageable) {
        return zoneRepository.findByEventoId(eventId, pageable)
                .map(zoneMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ZoneResponseDTO findById(UUID id) {
        return zoneRepository.findById(id)
                .map(zoneMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Zona no encontrada con ID: " + id));
    }

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

    @Override
    @Transactional(readOnly = true)
    public Page<ZoneResponseDTO> findAvailableZonesByEventId(UUID eventId, Pageable pageable) {
        return zoneRepository.findAvailableZonesByEventId(eventId, pageable)
                .map(zoneMapper::toDto);
    }
}