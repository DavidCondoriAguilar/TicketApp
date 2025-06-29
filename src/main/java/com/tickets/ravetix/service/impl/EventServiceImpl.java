package com.tickets.ravetix.service.impl;

import com.tickets.ravetix.dto.event.EventCreateDTO;
import com.tickets.ravetix.dto.event.EventResponseDTO;
import com.tickets.ravetix.dto.event.EventUpdateDTO;
import com.tickets.ravetix.dto.mapper.EventMapper;
import com.tickets.ravetix.entity.Event;
import com.tickets.ravetix.enums.EstadoEvento;
import com.tickets.ravetix.exception.event.EventException;
import com.tickets.ravetix.exception.ResourceNotFoundException;
import com.tickets.ravetix.repository.EventRepository;
import com.tickets.ravetix.service.interfac.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tickets.ravetix.util.EventStatisticsCalculator;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    /**
     * Obtiene todos los eventos paginados.
     *
     * @param pageable Parámetro de paginación y ordenamiento.
     * @return Página de eventos encontrados.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EventResponseDTO> findAll(Pageable pageable) {
        log.debug("Fetching all events with pagination: {}", pageable);
        Page<Event> events = eventRepository.findAll(pageable);
        
        // Actualizar estadísticas de cada evento
        events.getContent().forEach(event -> {
            EventStatisticsCalculator.calculateEventStatistics(event);
            event.getZonas().forEach(zone -> 
                EventStatisticsCalculator.calculateZoneStatistics(zone));
        });
        
        return events.map(eventMapper::toDto);
    }

    /**
     * Busca un evento por su identificador único.
     *
     * @param id Identificador único del evento.
     * @return DTO con los datos del evento encontrado.
     * @throws ResourceNotFoundException si el evento no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public EventResponseDTO findById(UUID id) {
        log.debug("Fetching event with id: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));
        return eventMapper.toDto(event);
    }

    /**
     * Crea un nuevo evento a partir de una entidad Event, validando fechas y estado.
     *
     * @param event Entidad Event a crear.
     * @return DTO con los datos del evento creado.
     * @throws EventException si las fechas o duración no son válidas.
     */
    @Override
    @Transactional
    public EventResponseDTO create(Event event) {
        log.debug("Creating new event from entity: {}", event.getNombre());
        
        // Validar que las fechas del evento sean futuras
        LocalDateTime now = LocalDateTime.now();
        if (event.getFechaHoraInicio() != null && event.getFechaHoraInicio().isBefore(now)) {
            throw new EventException("Invalid event start date", "Event start date must be in the future");
        }
        
        if (event.getFechaHoraFin() != null && event.getFechaHoraFin().isBefore(now)) {
            throw new EventException("Invalid event end date", "Event end date must be in the future");
        }
        
        // Validar que la fecha de fin sea posterior a la de inicio
        if (event.getFechaHoraInicio() != null && event.getFechaHoraFin() != null && 
            !event.getFechaHoraFin().isAfter(event.getFechaHoraInicio())) {
            throw new EventException("Invalid event dates", "Event end date must be after start date");
        }
        
        // Validar duración
        if (event.getDuracionHoras() != null && event.getDuracionHoras() <= 0) {
            throw new EventException("Invalid duration", "Duration must be greater than 0 hours");
        }
        
        // Asegurar que el estado esté establecido
        if (event.getEstado() == null) {
            event.setEstado(EstadoEvento.CREADO);
        }
        
        Event savedEvent = eventRepository.save(event);
        log.info("Created event with id: {}", savedEvent.getId());
        
        return eventMapper.toDto(savedEvent);
    }
    
    /**
     * Crea un nuevo evento a partir de un DTO de creación.
     *
     * @param createDto DTO con los datos para crear el evento.
     * @return DTO con los datos del evento creado.
     * @throws EventException si las fechas no son válidas.
     */
    @Transactional
    public EventResponseDTO createFromDto(EventCreateDTO createDto) {
        log.debug("Creating new event from DTO: {}", createDto.getNombre());
        
        // Validar fechas
        if (createDto.getFechaHoraFin().isBefore(createDto.getFechaHoraInicio())) {
            throw new EventException("Invalid event dates", "End date must be after start date");
        }
        
        Event event = eventMapper.toEntity(createDto);
        event.setEstado(EstadoEvento.CREADO); // Estado inicial
        
        return create(event);
    }

    /**
     * Actualiza los datos de un evento existente, validando que no esté finalizado o cancelado.
     *
     * @param id Identificador único del evento a actualizar.
     * @param updateDto DTO con los nuevos datos del evento.
     * @return DTO con los datos del evento actualizado.
     * @throws ResourceNotFoundException si el evento no existe.
     * @throws EventException si el evento no puede ser modificado.
     */
    @Override
    @Transactional
    public EventResponseDTO update(UUID id, EventUpdateDTO updateDto) {
        log.debug("Updating event with id: {}", id);
        
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));
        
        // Validar que no se pueda modificar un evento finalizado o cancelado
        if (existingEvent.getEstado() == EstadoEvento.FINALIZADO || 
            existingEvent.getEstado() == EstadoEvento.CANCELADO) {
            throw new EventException(
                "Cannot update event", 
                String.format("Event with id %s is %s and cannot be modified", 
                    id, existingEvent.getEstado().name().toLowerCase())
            );
        }
        
        eventMapper.updateEntity(updateDto, existingEvent);
        Event updatedEvent = eventRepository.save(existingEvent);
        log.info("Updated event with id: {}", id);
        
        return eventMapper.toDto(updatedEvent);
    }

    /**
     * Elimina un evento por su identificador único.
     *
     * @param id Identificador único del evento a eliminar.
     * @throws ResourceNotFoundException si el evento no existe.
     */
    @Override
    @Transactional
    public void delete(UUID id) {
        log.debug("Deleting event with id: {}", id);
        
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event", "id", id);
        }
        
        // Aquí podrías agregar lógica adicional, como verificar si hay tickets vendidos
        
        eventRepository.deleteById(id);
        log.info("Deleted event with id: {}", id);
    }

    /**
     * Obtiene eventos filtrados por estado.
     *
     * @param estado Estado del evento a filtrar.
     * @param pageable Parámetro de paginación y ordenamiento.
     * @return Página de eventos encontrados.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EventResponseDTO> findByEstado(EstadoEvento estado, Pageable pageable) {
        log.debug("Fetching events with status: {}", estado);
        return eventRepository.findByEstado(estado, pageable)
                .map(eventMapper::toDto);
    }

    /**
     * Obtiene eventos entre dos fechas dadas.
     *
     * @param startDate Fecha de inicio.
     * @param endDate Fecha de fin.
     * @param pageable Parámetro de paginación y ordenamiento.
     * @return Página de eventos encontrados.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EventResponseDTO> findBetweenDates(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.debug("Fetching events between {} and {}", startDate, endDate);
        return eventRepository.findBetweenDates(startDate, endDate, pageable)
                .map(eventMapper::toDto);
    }

    /**
     * Busca eventos por un texto de búsqueda.
     *
     * @param query Texto de búsqueda.
     * @param pageable Parámetro de paginación y ordenamiento.
     * @return Página de eventos encontrados.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EventResponseDTO> search(String query, Pageable pageable) {
        log.debug("Searching events with query: {}", query);
        return eventRepository.search(query, pageable)
                .map(eventMapper::toDto);
    }

    /**
     * Cambia el estado de un evento, validando la transición de estado.
     *
     * @param eventId Identificador único del evento.
     * @param newStatus Nuevo estado a establecer.
     * @return DTO con los datos del evento actualizado.
     * @throws ResourceNotFoundException si el evento no existe.
     * @throws EventException si la transición de estado no es válida.
     */
    @Override
    @Transactional
    public EventResponseDTO changeStatus(UUID eventId, EstadoEvento newStatus) {
        log.debug("Changing status of event {} to {}", eventId, newStatus);
        
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        
        // Validar transición de estado
        if (!isValidStatusTransition(event.getEstado(), newStatus)) {
            throw new EventException(
                "Invalid status transition",
                String.format("Cannot change status from %s to %s", 
                    event.getEstado(), newStatus)
            );
        }
        
        event.setEstado(newStatus);
        Event updatedEvent = eventRepository.save(event);
        log.info("Changed status of event {} to {}", eventId, newStatus);
        
        return eventMapper.toDto(updatedEvent);
    }


    /**
     * Valida si la transición de estado de un evento es permitida.
     *
     * @param current Estado actual.
     * @param newStatus Nuevo estado propuesto.
     * @return true si la transición es válida, false en caso contrario.
     */
    private boolean isValidStatusTransition(EstadoEvento current, EstadoEvento newStatus) {
        // Implementar lógica de transiciones de estado válidas
        // Por ejemplo, no se puede cambiar de CANCELADO a cualquier otro estado
        if (current == EstadoEvento.CANCELADO) {
            return false;
        }
        
        // No se puede volver a un estado anterior
        if (newStatus.ordinal() < current.ordinal() &&
            current != EstadoEvento.CREADO) {
            return false;
        }
        
        return true;
    }
}
