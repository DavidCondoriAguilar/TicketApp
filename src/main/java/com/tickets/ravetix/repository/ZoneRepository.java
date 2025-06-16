package com.tickets.ravetix.repository;

import com.tickets.ravetix.entity.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link Zone} entity.
 * Provides methods to perform database operations on zones.
 */
@Repository
public interface ZoneRepository extends BaseRepository<Zone, UUID> {
    
    /**
     * Find all zones for a specific event.
     *
     * @param eventId  the ID of the event
     * @param pageable pagination information
     * @return a page of zones for the event
     */
    Page<Zone> findByEventoId(UUID eventId, Pageable pageable);
    
    /**
     * Find all zones for a specific event with available capacity.
     *
     * @param eventId  the ID of the event
     * @param pageable pagination information
     * @return a page of zones with available capacity
     */
    @Query("SELECT z FROM Zone z WHERE z.evento.id = :eventId AND z.capacidad > (SELECT COUNT(t) FROM Ticket t WHERE t.zona.id = z.id)")
    Page<Zone> findAvailableZonesByEventId(@Param("eventId") UUID eventId, Pageable pageable);
    
    /**
     * Find a zone by event ID and zone name (case-insensitive).
     *
     * @param eventId the ID of the event
     * @param nombre  the name of the zone (case-insensitive)
     * @return an Optional containing the zone if found
     */
    @Query("SELECT z FROM Zone z WHERE z.evento.id = :eventId AND LOWER(z.nombre) = LOWER(:nombre)")
    Optional<Zone> findByEventIdAndNameIgnoreCase(
        @Param("eventId") UUID eventId,
        @Param("nombre") String nombre
    );
    
    /**
     * Count the number of tickets sold for a specific zone.
     *
     * @param zoneId the ID of the zone
     * @return the number of tickets sold for the zone
     */
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.zona.id = :zoneId")
    long countTicketsByZoneId(@Param("zoneId") UUID zoneId);
    
    /**
     * Find all zones that match the given criteria.
     *
     * @param eventoId the ID of the event (optional)
     * @param nombre   the name of the zone (optional, case-insensitive partial match)
     * @param precioMin the minimum price (inclusive)
     * @param precioMax the maximum price (inclusive)
     * @return a list of zones matching the criteria
     */
    @Query("SELECT z FROM Zone z WHERE " +
           "(:eventoId IS NULL OR z.evento.id = :eventoId) AND " +
           "(:nombre IS NULL OR LOWER(z.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(z.precioBase BETWEEN :precioMin AND :precioMax)")
    List<Zone> findZonesByCriteria(
        @Param("eventoId") UUID eventoId,
        @Param("nombre") String nombre,
        @Param("precioMin") double precioMin,
        @Param("precioMax") double precioMax
    );
    
    /**
     * Check if a zone with the given name exists for an event.
     */
    @Query("SELECT CASE WHEN COUNT(z) > 0 THEN true ELSE false END FROM Zone z WHERE z.evento.id = :eventoId AND LOWER(z.nombre) = LOWER(:nombre)")
    boolean existsByEventoIdAndNombre(@Param("eventoId") UUID eventoId, @Param("nombre") String nombre);
    
    /**
     * Check if another zone with the same name exists for an event (excluding the current zone).
     */
    @Query("SELECT CASE WHEN COUNT(z) > 0 THEN true ELSE false END FROM Zone z WHERE z.evento.id = :eventoId AND LOWER(z.nombre) = LOWER(:nombre) AND z.id != :excludeId")
    boolean existsByEventoIdAndNombreAndIdNot(@Param("eventoId") UUID eventoId, @Param("nombre") String nombre, @Param("excludeId") UUID excludeId);
    
    /**
     * Find a zone by ID with its tickets eagerly loaded.
     */
    @Query("SELECT z FROM Zone z LEFT JOIN FETCH z.tickets WHERE z.id = :id")
    Optional<Zone> findByIdWithTickets(@Param("id") UUID id);
}
