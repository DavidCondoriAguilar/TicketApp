package com.tickets.ravetix.repository;

import com.tickets.ravetix.entity.Payment;
import com.tickets.ravetix.entity.Ticket;
import com.tickets.ravetix.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link Payment} entity.
 * Provides methods to perform database operations on payments.
 */
@Repository
public interface PaymentRepository extends BaseRepository<Payment, UUID> {
    
    /**
     * Find all payments for a specific user.
     *
     * @param userId   the ID of the user
     * @param pageable pagination information
     * @return a page of payments made by the user
     */
    Page<Payment> findByUsuarioId(UUID userId, Pageable pageable);
    
    /**
     * Find all payments for a specific ticket.
     *
     * @param ticketId the ID of the ticket
     * @return a list of payments for the ticket
     */
    List<Payment> findByTicketId(UUID ticketId);
    
    /**
     * Find payments within a date range.
     *
     * @param startDate the start date (inclusive)
     * @param endDate   the end date (inclusive)
     * @return a list of payments within the date range
     */
    List<Payment> findByFechaPagoBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Calculate the total amount paid for a specific ticket.
     *
     * @param ticketId the ID of the ticket
     * @return the total amount paid for the ticket
     */
    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Payment p WHERE p.ticket.id = :ticketId AND p.estado = 'COMPLETADO'")
    BigDecimal sumPaidAmountByTicketId(@Param("ticketId") UUID ticketId);
    
    /**
     * Find a payment by ID and load its ticket relationship.
     *
     * @param id the ID of the payment
     * @return the payment with its ticket loaded
     */
    @Query("SELECT p FROM Payment p " +
           "LEFT JOIN FETCH p.ticket t " +
           "LEFT JOIN FETCH t.zona z " +
           "LEFT JOIN FETCH z.evento " +
           "WHERE p.id = :id")
    Optional<Payment> findByIdWithTicketAndZone(@Param("id") UUID id);
    
    /**
     * Find a payment by ID.
     *
     * @param id the ID of the payment
     * @return the payment if found
     */
    @Override
    Optional<Payment> findById(UUID id);
    
    /**
     * Find all successful payments within a date range.
     *
     * @param startDate the start date (inclusive)
     * @param endDate   the end date (inclusive)
     * @param pageable  pagination information
     * @return a page of successful payments within the date range
     */
    Page<Payment> findByEstadoAndFechaPagoBetween(
        @Param("estado") String estado,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
}
