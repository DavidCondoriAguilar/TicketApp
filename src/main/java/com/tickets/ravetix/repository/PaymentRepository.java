package com.tickets.ravetix.repository;

import com.tickets.ravetix.entity.Payment;
import com.tickets.ravetix.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Payment p WHERE p.ticket.id = :ticketId")
    BigDecimal getTotalPaidForTicket(@Param("ticketId") UUID ticketId);
    
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
