package com.tickets.ravetix.service.interfac;

import com.tickets.ravetix.dto.event.EventCreateDTO;
import com.tickets.ravetix.dto.event.EventResponseDTO;
import com.tickets.ravetix.dto.event.EventUpdateDTO;
import com.tickets.ravetix.entity.Event;
import com.tickets.ravetix.enums.EstadoEvento;
import com.tickets.ravetix.service.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

public interface EventService extends BaseService<Event, UUID, Event, EventUpdateDTO, EventResponseDTO> {

    @Override
    @Transactional
    EventResponseDTO create(Event event);
    
    /**
     * Creates a new event from a DTO
     * @param createDto the DTO containing event data
     * @return the created event as a DTO
     */
    @Transactional
    EventResponseDTO createFromDto(EventCreateDTO createDto);

    @Override
    EventResponseDTO update(UUID id, EventUpdateDTO updateDto);

    @Override
    void delete(UUID id);

    @Override
    EventResponseDTO findById(UUID id);

    @Override
    Page<EventResponseDTO> findAll(Pageable pageable);

    /**
     * Busca eventos por estado
     */
    Page<EventResponseDTO> findByEstado(EstadoEvento estado, Pageable pageable);
    
    /**
     * Busca eventos entre fechas
     */
    Page<EventResponseDTO> findBetweenDates(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Busca eventos por término de búsqueda
     */
    Page<EventResponseDTO> search(String query, Pageable pageable);
    
    /**
     * Cambia el estado de un evento
     */
    EventResponseDTO changeStatus(UUID eventId, EstadoEvento newStatus);
}
