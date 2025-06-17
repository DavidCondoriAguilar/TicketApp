package com.tickets.ravetix.service.impl;

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
import com.tickets.ravetix.service.interfac.EventHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventHistoryServiceImpl implements EventHistoryService {

    private final EventHistoryRepository eventHistoryRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventHistoryMapper eventHistoryMapper;

    @Override
    @Transactional(readOnly = true)
    public EventHistoryResponseDTO getEventHistoryById(UUID id) {
        log.info("Fetching event history with ID: {}", id);
        return eventHistoryRepository.findById(id)
                .map(eventHistoryMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("EventHistory", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventHistoryResponseDTO> getEventHistoryByUserId(UUID userId, Pageable pageable) {
        log.info("Fetching event history for user ID: {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return eventHistoryRepository.findByUsuarioId(userId, pageable)
                .map(eventHistoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventHistoryResponseDTO> getEventHistoryByEventId(UUID eventId, Pageable pageable) {
        log.info("Fetching event history for event ID: {}", eventId);
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event", "id", eventId);
        }
        return eventHistoryRepository.findByEventoId(eventId, pageable)
                .map(eventHistoryMapper::toDto);
    }

    @Override
    @Transactional
    public EventHistoryResponseDTO confirmAttendance(UUID eventId, UUID userId) {
        log.info("Confirming attendance for user ID: {} at event ID: {}", userId, eventId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        
        // Verificar si ya existe un historial para este usuario y evento
        EventHistory eventHistory = eventHistoryRepository.findByUsuarioIdAndEventoId(userId, eventId)
                .orElseGet(() -> {
                    EventHistory newHistory = new EventHistory();
                    newHistory.setUsuario(user);
                    newHistory.setEvento(event);
                    return newHistory;
                });
        
        // Confirmar asistencia
        eventHistory.setAsistenciaConfirmada(true);
        eventHistory.setFechaConfirmacionAsistencia(LocalDateTime.now());
        
        EventHistory savedHistory = eventHistoryRepository.save(eventHistory);
        log.info("Attendance confirmed for user ID: {} at event ID: {}", userId, eventId);
        
        return eventHistoryMapper.toDto(savedHistory);
    }

    @Override
    @Transactional
    public EventHistoryResponseDTO rateEvent(UUID eventId, UUID userId, Integer rating, String comment) {
        log.info("Rating event ID: {} by user ID: {} with rating: {}", eventId, userId, rating);
        
        // Validar que el rating esté en el rango correcto (1-5)
        if (rating < 1 || rating > 5) {
            throw new ValidationException("Validación fallida", "La calificación debe estar entre 1 y 5");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        
        // Buscar el historial del usuario para este evento
        EventHistory eventHistory = eventHistoryRepository.findByUsuarioIdAndEventoId(userId, eventId)
                .orElseThrow(() -> new ValidationException("Validación fallida", "El usuario no tiene un historial para este evento"));
        
        // Actualizar calificación y comentario
        eventHistory.setCalificacion(rating);
        eventHistory.setComentario(comment);
        eventHistory.setFechaCalificacion(LocalDateTime.now());
        
        EventHistory savedHistory = eventHistoryRepository.save(eventHistory);
        log.info("Event ID: {} rated by user ID: {} with rating: {}", eventId, userId, rating);
        
        return eventHistoryMapper.toDto(savedHistory);
    }
}
