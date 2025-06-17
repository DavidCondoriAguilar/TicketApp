package com.tickets.ravetix.service;

import com.tickets.ravetix.dto.mapper.ZoneMapper;
import com.tickets.ravetix.dto.zone.ZoneCreateDTO;
import com.tickets.ravetix.dto.zone.ZoneResponseDTO;
import com.tickets.ravetix.dto.zone.ZoneUpdateDTO;
import com.tickets.ravetix.entity.Event;
import com.tickets.ravetix.entity.Ticket;
import com.tickets.ravetix.entity.Zone;
import com.tickets.ravetix.exception.NotFoundException;
import com.tickets.ravetix.exception.ValidationException;
import com.tickets.ravetix.repository.EventRepository;
import com.tickets.ravetix.repository.ZoneRepository;
import com.tickets.ravetix.service.impl.ZoneServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ZoneServiceTest {

    @Mock
    private ZoneRepository zoneRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private ZoneMapper zoneMapper;

    @InjectMocks
    private ZoneServiceImpl zoneService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById_deberiaRetornarZoneResponseDTO_siExiste() {
        UUID id = UUID.randomUUID();
        Zone zone = new Zone();
        zone.setId(id);
        ZoneResponseDTO dto = new ZoneResponseDTO();
        when(zoneRepository.findById(id)).thenReturn(Optional.of(zone));
        when(zoneMapper.toDto(zone)).thenReturn(dto);

        ZoneResponseDTO result = zoneService.findById(id);

        assertNotNull(result);
        verify(zoneRepository).findById(id);
        verify(zoneMapper).toDto(zone);
    }

    @Test
    void findById_deberiaLanzarNotFoundException_siNoExiste() {
        UUID id = UUID.randomUUID();
        when(zoneRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> zoneService.findById(id));
        verify(zoneRepository).findById(id);
    }

    @Test
    void create_deberiaCrearZone_siNoExisteNombreRepetido() {
        UUID eventId = UUID.randomUUID();
        ZoneCreateDTO dto = new ZoneCreateDTO();
        // Suponiendo que tiene setters
        dto.setEventoId(eventId);
        dto.setNombre("VIP");

        Event event = new Event();
        event.setId(eventId);

        Zone zone = new Zone();
        zone.setEvento(event);

        Zone savedZone = new Zone();
        ZoneResponseDTO responseDTO = new ZoneResponseDTO();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(zoneRepository.existsByEventoIdAndNombre(eventId, "VIP")).thenReturn(false);
        when(zoneMapper.toEntity(dto)).thenReturn(zone);
        when(zoneRepository.save(zone)).thenReturn(savedZone);
        when(zoneMapper.toDto(savedZone)).thenReturn(responseDTO);

        ZoneResponseDTO result = zoneService.create(dto);

        assertNotNull(result);
        verify(eventRepository).findById(eventId);
        verify(zoneRepository).existsByEventoIdAndNombre(eventId, "VIP");
        verify(zoneRepository).save(zone);
        verify(zoneMapper).toDto(savedZone);
    }

    @Test
    void create_deberiaLanzarValidationException_siNombreRepetido() {
        UUID eventId = UUID.randomUUID();
        ZoneCreateDTO dto = new ZoneCreateDTO();
        dto.setEventoId(eventId);
        dto.setNombre("VIP");

        Event event = new Event();
        event.setId(eventId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(zoneRepository.existsByEventoIdAndNombre(eventId, "VIP")).thenReturn(true);

        assertThrows(ValidationException.class, () -> zoneService.create(dto));
        verify(zoneRepository).existsByEventoIdAndNombre(eventId, "VIP");
    }

    @Test
    void update_deberiaActualizarZone_siNoExisteNombreRepetido() {
        UUID id = UUID.randomUUID();
        ZoneUpdateDTO dto = new ZoneUpdateDTO();
        dto.setNombre("General");

        Zone zone = new Zone();
        zone.setId(id);
        Event event = new Event();
        event.setId(UUID.randomUUID());
        zone.setEvento(event);

        Zone updatedZone = new Zone();
        ZoneResponseDTO responseDTO = new ZoneResponseDTO();

        when(zoneRepository.findById(id)).thenReturn(Optional.of(zone));
        when(zoneRepository.existsByEventoIdAndNombreAndIdNot(event.getId(), "General", id)).thenReturn(false);
        doNothing().when(zoneMapper).updateZoneFromDto(dto, zone);
        when(zoneRepository.save(zone)).thenReturn(updatedZone);
        when(zoneMapper.toDto(updatedZone)).thenReturn(responseDTO);

        ZoneResponseDTO result = zoneService.update(id, dto);

        assertNotNull(result);
        verify(zoneRepository).findById(id);
        verify(zoneRepository).existsByEventoIdAndNombreAndIdNot(event.getId(), "General", id);
        verify(zoneMapper).updateZoneFromDto(dto, zone);
        verify(zoneRepository).save(zone);
        verify(zoneMapper).toDto(updatedZone);
    }

    @Test
    void update_deberiaLanzarValidationException_siNombreRepetido() {
        UUID id = UUID.randomUUID();
        ZoneUpdateDTO dto = new ZoneUpdateDTO();
        dto.setNombre("General");

        Zone zone = new Zone();
        zone.setId(id);
        Event event = new Event();
        event.setId(UUID.randomUUID());
        zone.setEvento(event);

        when(zoneRepository.findById(id)).thenReturn(Optional.of(zone));
        when(zoneRepository.existsByEventoIdAndNombreAndIdNot(event.getId(), "General", id)).thenReturn(true);

        assertThrows(ValidationException.class, () -> zoneService.update(id, dto));
        verify(zoneRepository).existsByEventoIdAndNombreAndIdNot(event.getId(), "General", id);
    }

    @Test
    void delete_deberiaEliminarZone_siNoTieneTickets() {
        UUID id = UUID.randomUUID();
        Zone zone = new Zone();
        zone.setId(id);
        zone.setTickets(Collections.emptyList());

        when(zoneRepository.findByIdWithTickets(id)).thenReturn(Optional.of(zone));
        doNothing().when(zoneRepository).delete(zone);

        assertDoesNotThrow(() -> zoneService.delete(id));
        verify(zoneRepository).findByIdWithTickets(id);
        verify(zoneRepository).delete(zone);
    }

    @Test
    void delete_deberiaLanzarValidationException_siTieneTickets() {
        UUID id = UUID.randomUUID();
        Zone zone = new Zone();
        zone.setId(id);
        zone.setTickets(List.of(new Ticket())); // Simula que tiene tickets

        when(zoneRepository.findByIdWithTickets(id)).thenReturn(Optional.of(zone));

        assertThrows(ValidationException.class, () -> zoneService.delete(id));
        verify(zoneRepository).findByIdWithTickets(id);
    }

    @Test
    void delete_deberiaLanzarNotFoundException_siNoExiste() {
        UUID id = UUID.randomUUID();
        when(zoneRepository.findByIdWithTickets(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> zoneService.delete(id));
        verify(zoneRepository).findByIdWithTickets(id);
    }

    @Test
    void findAllByEventId_deberiaRetornarPageDeZoneResponseDTO() {
        UUID eventId = UUID.randomUUID();
        Pageable pageable = Pageable.unpaged();
        Zone zone = new Zone();
        List<Zone> zones = List.of(zone);
        Page<Zone> page = new PageImpl<>(zones);
        ZoneResponseDTO dto = new ZoneResponseDTO();

        when(zoneRepository.findByEventoId(eventId, pageable)).thenReturn(page);
        when(zoneMapper.toDto(zone)).thenReturn(dto);

        Page<ZoneResponseDTO> result = zoneService.findAllByEventId(eventId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(zoneRepository).findByEventoId(eventId, pageable);
        verify(zoneMapper).toDto(zone);
    }

    @Test
    void findAvailableZonesByEventId_deberiaRetornarPageDeZoneResponseDTO() {
        UUID eventId = UUID.randomUUID();
        Pageable pageable = Pageable.unpaged();
        Zone zone = new Zone();
        List<Zone> zones = List.of(zone);
        Page<Zone> page = new PageImpl<>(zones);
        ZoneResponseDTO dto = new ZoneResponseDTO();

        when(zoneRepository.findAvailableZonesByEventId(eventId, pageable)).thenReturn(page);
        when(zoneMapper.toDto(zone)).thenReturn(dto);

        Page<ZoneResponseDTO> result = zoneService.findAvailableZonesByEventId(eventId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(zoneRepository).findAvailableZonesByEventId(eventId, pageable);
        verify(zoneMapper).toDto(zone);
    }
}