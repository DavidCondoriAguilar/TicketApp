package com.tickets.ravetix.service.interfac;

import com.tickets.ravetix.dto.eventhistory.EventHistoryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface EventHistoryService {
    
    /**
     * Get event history by ID
     * @param id history ID
     * @return event history details
     */
    EventHistoryResponseDTO getEventHistoryById(UUID id);
    
    /**
     * Get all event history for a user
     * @param userId user ID
     * @param pageable pagination information
     * @return page of event history
     */
    Page<EventHistoryResponseDTO> getEventHistoryByUserId(UUID userId, Pageable pageable);
    
    /**
     * Get all event history for an event
     * @param eventId event ID
     * @param pageable pagination information
     * @return page of event history
     */
    Page<EventHistoryResponseDTO> getEventHistoryByEventId(UUID eventId, Pageable pageable);
    
    /**
     * Confirm attendance for an event
     * @param eventId event ID
     * @param userId user ID
     * @return updated event history
     */
    EventHistoryResponseDTO confirmAttendance(UUID eventId, UUID userId);
    
    /**
     * Rate an event
     * @param eventId event ID
     * @param userId user ID
     * @param rating rating (1-5)
     * @param comment optional comment
     * @return updated event history
     */
    EventHistoryResponseDTO rateEvent(UUID eventId, UUID userId, Integer rating, String comment);
}
