package com.tickets.ravetix.repository;

import com.tickets.ravetix.entity.Ticket;
import com.tickets.ravetix.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for {@link Ticket} entity.
 * Provides methods to perform database operations on tickets.
 */
@Repository
public interface TicketRepository extends BaseRepository<Ticket, UUID> {
    
    /**
     * Find all tickets for a specific user.
     *
     * @param userId   the ID of the user
     * @param pageable pagination information
     * @return a page of tickets belonging to the user
     */
    Page<Ticket> findByUsuarioId(UUID userId, Pageable pageable);
    
    /**
     * Find all tickets for a specific event.
     *
     * @param eventId  the ID of the event
     * @param pageable pagination information
     * @return a page of tickets for the event
     */
    Page<Ticket> findByEventoId(UUID eventId, Pageable pageable);
    
    /**
     * Count the number of tickets sold for a specific event.
     *
     * @param eventId the ID of the event
     * @return the number of tickets sold
     */
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.evento.id = :eventId")
    long countByEventoId(@Param("eventId") UUID eventId);
    
    /**
     * Find all tickets for a specific user and event.
     *
     * @param userId  the ID of the user
     * @param eventId the ID of the event
     * @return a list of tickets matching the criteria
     */
    List<Ticket> findByUsuarioIdAndEventoId(UUID userId, UUID eventId);
    
    /**
     * Check if a user has already purchased a ticket for an event.
     *
     * @param userId  the ID of the user
     * @param eventId the ID of the event
     * @return true if the user has already purchased a ticket for the event
     */
    boolean existsByUsuarioIdAndEventoId(UUID userId, UUID eventId);
}
