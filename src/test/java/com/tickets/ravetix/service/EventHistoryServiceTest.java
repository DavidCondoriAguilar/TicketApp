
package com.tickets.ravetix.service;

import com.tickets.ravetix.dto.eventhistory.EventHistoryResponseDTO;
import com.tickets.ravetix.dto.mapper.EventHistoryMapper;
import com.tickets.ravetix.entity.Event;
import com.tickets.ravetix.entity.EventHistory;
import com.tickets.ravetix.entity.User;
import com.tickets.ravetix.exception.ResourceNotFoundException;
import com.tickets.ravetix.exception.ValidationException;
import com.tickets.ravetix.repository.EventHistoryRepository;
import com.tickets.ravetix.repository.EventRepository;
import com.tickets.ravetix.repository.UserRepository;
import com.tickets.ravetix.service.impl.EventHistoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventHistoryServiceTest {

    @Mock
    private EventHistoryRepository eventHistoryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventHistoryMapper eventHistoryMapper;

    @InjectMocks
    private EventHistoryServiceImpl eventHistoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getEventHistoryById_shouldReturnDTO_whenExists() {
        UUID id = UUID.randomUUID();
        EventHistory eventHistory = new EventHistory();
        EventHistoryResponseDTO dto = new EventHistoryResponseDTO();
        when(eventHistoryRepository.findById(id)).thenReturn(Optional.of(eventHistory));
        when(eventHistoryMapper.toDto(eventHistory)).thenReturn(dto);

        EventHistoryResponseDTO result = eventHistoryService.getEventHistoryById(id);

        assertNotNull(result);
        verify(eventHistoryRepository).findById(id);
    }

    @Test
    void getEventHistoryById_shouldThrow_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(eventHistoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventHistoryService.getEventHistoryById(id));
    }

    @Test
    void getEventHistoryByUserId_shouldReturnPage_whenUserExists() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        EventHistory eventHistory = new EventHistory();
        EventHistoryResponseDTO dto = new EventHistoryResponseDTO();
        Page<EventHistory> page = new PageImpl<>(List.of(eventHistory));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(eventHistoryRepository.findByUsuarioId(userId, pageable)).thenReturn(page);
        when(eventHistoryMapper.toDto(eventHistory)).thenReturn(dto);

        Page<EventHistoryResponseDTO> result = eventHistoryService.getEventHistoryByUserId(userId, pageable);

        assertEquals(1, result.getTotalElements());
        verify(userRepository).existsById(userId);
    }

    @Test
    void getEventHistoryByUserId_shouldThrow_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> eventHistoryService.getEventHistoryByUserId(userId, pageable));
    }

    @Test
    void getEventHistoryByEventId_shouldReturnPage_whenEventExists() {
        UUID eventId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        EventHistory eventHistory = new EventHistory();
        EventHistoryResponseDTO dto = new EventHistoryResponseDTO();
        Page<EventHistory> page = new PageImpl<>(List.of(eventHistory));
        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(eventHistoryRepository.findByEventoId(eventId, pageable)).thenReturn(page);
        when(eventHistoryMapper.toDto(eventHistory)).thenReturn(dto);

        Page<EventHistoryResponseDTO> result = eventHistoryService.getEventHistoryByEventId(eventId, pageable);

        assertEquals(1, result.getTotalElements());
        verify(eventRepository).existsById(eventId);
    }

    @Test
    void getEventHistoryByEventId_shouldThrow_whenEventNotFound() {
        UUID eventId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        when(eventRepository.existsById(eventId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> eventHistoryService.getEventHistoryByEventId(eventId, pageable));
    }

    @Test
    void confirmAttendance_shouldCreateOrUpdateHistory() {
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        User user = new User();
        Event event = new Event();
        EventHistory eventHistory = new EventHistory();
        EventHistoryResponseDTO dto = new EventHistoryResponseDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventHistoryRepository.findByUsuarioIdAndEventoId(userId, eventId)).thenReturn(Optional.empty());
        when(eventHistoryRepository.save(any(EventHistory.class))).thenReturn(eventHistory);
        when(eventHistoryMapper.toDto(eventHistory)).thenReturn(dto);

        EventHistoryResponseDTO result = eventHistoryService.confirmAttendance(eventId, userId);

        assertNotNull(result);
        verify(eventHistoryRepository).save(any(EventHistory.class));
    }

    @Test
    void confirmAttendance_shouldUpdateExistingHistory() {
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        User user = new User();
        Event event = new Event();
        EventHistory eventHistory = new EventHistory();
        EventHistoryResponseDTO dto = new EventHistoryResponseDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventHistoryRepository.findByUsuarioIdAndEventoId(userId, eventId)).thenReturn(Optional.of(eventHistory));
        when(eventHistoryRepository.save(eventHistory)).thenReturn(eventHistory);
        when(eventHistoryMapper.toDto(eventHistory)).thenReturn(dto);

        EventHistoryResponseDTO result = eventHistoryService.confirmAttendance(eventId, userId);

        assertNotNull(result);
        verify(eventHistoryRepository).save(eventHistory);
    }

    @Test
    void confirmAttendance_shouldThrow_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventHistoryService.confirmAttendance(eventId, userId));
    }

    @Test
    void confirmAttendance_shouldThrow_whenEventNotFound() {
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventHistoryService.confirmAttendance(eventId, userId));
    }

    @Test
    void rateEvent_shouldUpdateRating_whenValid() {
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        User user = new User();
        Event event = new Event();
        EventHistory eventHistory = new EventHistory();
        EventHistoryResponseDTO dto = new EventHistoryResponseDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventHistoryRepository.findByUsuarioIdAndEventoId(userId, eventId)).thenReturn(Optional.of(eventHistory));
        when(eventHistoryRepository.save(eventHistory)).thenReturn(eventHistory);
        when(eventHistoryMapper.toDto(eventHistory)).thenReturn(dto);

        EventHistoryResponseDTO result = eventHistoryService.rateEvent(eventId, userId, 5, "Muy bueno");

        assertNotNull(result);
        verify(eventHistoryRepository).save(eventHistory);
    }

    @Test
    void rateEvent_shouldThrow_whenRatingInvalid() {
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        assertThrows(ValidationException.class, () -> eventHistoryService.rateEvent(eventId, userId, 0, "Malo"));
        assertThrows(ValidationException.class, () -> eventHistoryService.rateEvent(eventId, userId, 6, "Excelente"));
    }

    @Test
    void rateEvent_shouldThrow_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventHistoryService.rateEvent(eventId, userId, 4, "Bien"));
    }

    @Test
    void rateEvent_shouldThrow_whenEventNotFound() {
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventHistoryService.rateEvent(eventId, userId, 4, "Bien"));
    }

    @Test
    void rateEvent_shouldThrow_whenNoHistory() {
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        User user = new User();
        Event event = new Event();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventHistoryRepository.findByUsuarioIdAndEventoId(userId, eventId)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> eventHistoryService.rateEvent(eventId, userId, 4, "Bien"));
    }
}