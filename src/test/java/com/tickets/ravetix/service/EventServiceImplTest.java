// src/test/java/com/tickets/ravetix/service/impl/EventServiceImplTest.java
package com.tickets.ravetix.service;

import com.tickets.ravetix.dto.event.EventCreateDTO;
import com.tickets.ravetix.dto.event.EventResponseDTO;
import com.tickets.ravetix.dto.event.EventUpdateDTO;
import com.tickets.ravetix.dto.mapper.EventMapper;
import com.tickets.ravetix.entity.Event;
import com.tickets.ravetix.enums.EstadoEvento;
import com.tickets.ravetix.exception.event.EventException;
import com.tickets.ravetix.exception.ResourceNotFoundException;
import com.tickets.ravetix.repository.EventRepository;
import com.tickets.ravetix.service.impl.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventServiceImpl eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_shouldSaveEvent_whenValid() {
        Event event = new Event();
        event.setNombre("Test Event");
        event.setFechaHoraInicio(LocalDateTime.now().plusDays(1));
        event.setFechaHoraFin(LocalDateTime.now().plusDays(2));
        event.setDuracionHoras(2);
        Event saved = new Event();
        saved.setId(UUID.randomUUID());
        saved.setNombre("Test Event");
        when(eventRepository.save(event)).thenReturn(saved);
        when(eventMapper.toDto(saved)).thenReturn(new EventResponseDTO());

        EventResponseDTO result = eventService.create(event);

        assertNotNull(result);
        verify(eventRepository).save(event);
    }

    @Test
    void create_shouldThrowException_whenStartDateInPast() {
        Event event = new Event();
        event.setNombre("Test Event");
        event.setFechaHoraInicio(LocalDateTime.now().minusDays(1));
        event.setFechaHoraFin(LocalDateTime.now().plusDays(1));
        event.setDuracionHoras(2);

        assertThrows(EventException.class, () -> eventService.create(event));
    }

    @Test
    void findById_shouldReturnEvent_whenExists() {
        UUID id = UUID.randomUUID();
        Event event = new Event();
        event.setId(id);
        when(eventRepository.findById(id)).thenReturn(Optional.of(event));
        when(eventMapper.toDto(event)).thenReturn(new EventResponseDTO());

        EventResponseDTO result = eventService.findById(id);

        assertNotNull(result);
        verify(eventRepository).findById(id);
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(eventRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.findById(id));
    }

    @Test
    void update_shouldUpdate_whenValid() {
        UUID id = UUID.randomUUID();
        EventUpdateDTO updateDTO = new EventUpdateDTO();
        Event event = new Event();
        event.setId(id);
        event.setEstado(EstadoEvento.CREADO);
        when(eventRepository.findById(id)).thenReturn(Optional.of(event));
        doNothing().when(eventMapper).updateEntity(updateDTO, event);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(new EventResponseDTO());

        EventResponseDTO result = eventService.update(id, updateDTO);

        assertNotNull(result);
        verify(eventRepository).save(event);
    }

    @Test
    void update_shouldThrow_whenFinalizado() {
        UUID id = UUID.randomUUID();
        EventUpdateDTO updateDTO = new EventUpdateDTO();
        Event event = new Event();
        event.setId(id);
        event.setEstado(EstadoEvento.FINALIZADO);
        when(eventRepository.findById(id)).thenReturn(Optional.of(event));

        assertThrows(EventException.class, () -> eventService.update(id, updateDTO));
    }

    @Test
    void delete_shouldRemove_whenExists() {
        UUID id = UUID.randomUUID();
        when(eventRepository.existsById(id)).thenReturn(true);
        doNothing().when(eventRepository).deleteById(id);

        eventService.delete(id);

        verify(eventRepository).deleteById(id);
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(eventRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> eventService.delete(id));
    }

    @Test
    void changeStatus_shouldUpdateStatus_whenValidTransition() {
        UUID id = UUID.randomUUID();
        Event event = new Event();
        event.setId(id);
        event.setEstado(EstadoEvento.CREADO);
        when(eventRepository.findById(id)).thenReturn(Optional.of(event));
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(new EventResponseDTO());

        EventResponseDTO result = eventService.changeStatus(id, EstadoEvento.EN_CURSO);

        assertNotNull(result);
        assertEquals(EstadoEvento.EN_CURSO, event.getEstado());
    }

    @Test
    void changeStatus_shouldThrow_whenInvalidTransition() {
        UUID id = UUID.randomUUID();
        Event event = new Event();
        event.setId(id);
        event.setEstado(EstadoEvento.CANCELADO);
        when(eventRepository.findById(id)).thenReturn(Optional.of(event));

        assertThrows(EventException.class, () -> eventService.changeStatus(id, EstadoEvento.CREADO));
    }
}