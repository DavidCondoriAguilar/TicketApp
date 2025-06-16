package com.tickets.ravetix.repository;

import com.tickets.ravetix.entity.Event;
import com.tickets.ravetix.enums.EstadoEvento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for {@link Event} entity.
 * Provides methods to perform database operations on events.
 */
@Repository
public interface EventRepository extends BaseRepository<Event, UUID> {
    
    /**
     * Find events by status with pagination.
     *
     * @param estado   the event status to filter by
     * @param pageable pagination information
     * @return a page of events with the given status
     */
    Page<Event> findByEstado(EstadoEvento estado, Pageable pageable);
    
    /**
     * Find events between two dates.
     *
     * @param startDate the start date (inclusive)
     * @param endDate   the end date (inclusive)
     * @return a list of events within the date range
     */
    /**
     * Find events between two dates with pagination.
     *
     * @param startDate the start date (inclusive)
     * @param endDate   the end date (inclusive)
     * @param pageable  pagination information
     * @return a page of events within the date range
     */
    @Query("SELECT e FROM Event e WHERE e.fechaHoraInicio BETWEEN :startDate AND :endDate")
    Page<Event> findBetweenDates(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
    
    /**
     * Search events by name or description (case-insensitive).
     *
     * @param query    the search term
     * @param pageable pagination information
     * @return a page of matching events
     */
    @Query("SELECT e FROM Event e WHERE LOWER(e.nombre) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(e.descripcion) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Event> search(@Param("query") String query, Pageable pageable);
    
    /**
     * Find upcoming events (after the current date).
     *
     * @param pageable pagination information
     * @return a page of upcoming events
     */
    @Query("SELECT e FROM Event e WHERE e.fechaHoraInicio > CURRENT_TIMESTAMP")
    Page<Event> findUpcomingEvents(Pageable pageable);
}
