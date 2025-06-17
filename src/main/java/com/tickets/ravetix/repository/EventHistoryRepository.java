package com.tickets.ravetix.repository;

import com.tickets.ravetix.entity.EventHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link EventHistory} entity.
 * Provides methods to perform database operations on event history entries.
 */
@Repository
public interface EventHistoryRepository extends BaseRepository<EventHistory, UUID> {
    
    /**
     * Find all history entries for a specific user.
     *
     * @param userId   the ID of the user
     * @param pageable pagination information
     * @return a page of history entries for the user
     */
    Page<EventHistory> findByUsuarioId(UUID userId, Pageable pageable);
    
    /**
     * Find all history entries for a specific event.
     *
     * @param eventId  the ID of the event
     * @param pageable pagination information
     * @return a page of history entries for the event
     */
    Page<EventHistory> findByEventoId(UUID eventId, Pageable pageable);
    
    /**
     * Find history entries within a date range based on creation date.
     *
     * @param startDate the start date (inclusive)
     * @param endDate   the end date (inclusive)
     * @return a list of history entries created within the date range
     */
    List<EventHistory> findByFechaCreacionBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find the most recent history entries for a user.
     *
     * @param userId   the ID of the user
     * @param limit    maximum number of entries to return
     * @return a list of the most recent history entries
     */
//    @Query("SELECT eh FROM EventHistory eh WHERE eh.usuario.id = :userId ORDER BY eh.fechaAccion DESC")
//    List<EventHistory> findRecentByUsuarioId(@Param("userId") UUID userId, Pageable limit);
    
    /**
     * Count the number of times a user has performed a specific action on an event.
     *
     * @param userId  the ID of the user
     * @param eventId the ID of the event
     * @param accion  the action to count
     * @return the count of matching history entries
//     */
//    @Query("SELECT COUNT(eh) FROM EventHistory eh WHERE eh.usuario.id = :userId AND eh.evento.id = :eventId AND eh.accion = :accion")
//    long countByUsuarioAndEventoAndAccion(
//        @Param("userId") UUID userId,
//        @Param("eventId") UUID eventId,
//        @Param("accion") String accion
//    );
    
    /**
     * Find a history entry by user ID and event ID.
     *
     * @param userId  the ID of the user
     * @param eventId the ID of the event
     * @return an Optional containing the event history if found
     */
    @Query("SELECT eh FROM EventHistory eh WHERE eh.usuario.id = :userId AND eh.evento.id = :eventId")
    Optional<EventHistory> findByUsuarioIdAndEventoId(@Param("userId") UUID userId, @Param("eventId") UUID eventId);
    
    /**
     * Find all history entries for a user's events.
     *
     * @param userId   the ID of the user
     * @param pageable pagination information
     * @return a page of history entries for the user's events
     */
//    @Query("SELECT eh FROM EventHistory eh WHERE eh.evento.organizador.id = :userId")
//    Page<EventHistory> findForUserEvents(@Param("userId") UUID userId, Pageable pageable);
}
