package com.tickets.ravetix.entity;

import com.tickets.ravetix.enums.TicketState;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a ticket for an event.
 * <p>
 * Contains information about the event, zone, user, and payment details.
 * Each ticket is associated with a specific event and zone.
 * </p>
 */
@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@ToString(exclude = {"evento", "zona", "usuario", "pago"})
public class Ticket extends BaseEntity {
    
    /**
     * Evento al que pertenece este ticket. Relación muchos a uno, obligatorio.
     */
    @NotNull(message = "El evento es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false, updatable = false)
    private Event evento;
    
    /**
     * Zona del evento asociada a este ticket. Relación muchos a uno, obligatorio.
     */
    @NotNull(message = "La zona es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "zona_id", nullable = false, updatable = false)
    private Zone zona;
    
    /**
     * Usuario que adquirió el ticket. Relación muchos a uno, obligatorio.
     */
    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    private User usuario;
    
    /**
     * Precio pagado por el ticket. Debe ser mayor a 0 y tener hasta 10 dígitos enteros y 2 decimales.
     */
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio debe tener máximo 10 dígitos enteros y 2 decimales")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    /**
     * Fecha y hora en que se realizó la compra del ticket. Se asigna automáticamente.
     */
    @Column(name = "fecha_compra", nullable = false, updatable = false)
    private LocalDateTime fechaCompra = LocalDateTime.now();
    
    /**
     * Pago asociado a este ticket. Relación uno a uno.
     */
    @OneToOne(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment pago;
    
    /**
     * Estado actual del ticket (ej: PENDIENTE_PAGO, PAGADO, CANCELADO, USADO).
     * Por defecto es PENDIENTE_PAGO.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketState estado = TicketState.PENDIENTE_PAGO;
    
    /**
     * Motivo de cancelación del ticket, si aplica (opcional).
     */
    @Column(name = "motivo_cancelacion", length = 500)
    private String motivoCancelacion;
    
    /**
     * Inicializa la fecha de compra antes de persistir la entidad.
     */
    @PrePersist
    protected void onCreate() {
        if (fechaCompra == null) {
            fechaCompra = LocalDateTime.now();
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ticket ticket)) return false;
        return getId() != null && getId().equals(ticket.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
