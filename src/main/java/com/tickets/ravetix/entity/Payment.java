package com.tickets.ravetix.entity;

import com.tickets.ravetix.enums.EstadoPago;
import com.tickets.ravetix.enums.MetodoPago;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a payment made by a user for a ticket.
 * <p>
 * Tracks payment details including amount, method, status, and related entities.
 * </p>
 */
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@ToString(exclude = {"ticket", "usuario"})
public class Payment extends BaseEntity {
    
    /**
     * Monto total del pago realizado por el usuario.
     * Debe ser mayor a 0 y tener hasta 10 dígitos enteros y 2 decimales.
     */
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El monto debe tener máximo 10 dígitos enteros y 2 decimales")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;
    
    /**
     * Método de pago utilizado (ej: TARJETA, PAYPAL, EFECTIVO).
     */
    @NotNull(message = "El método de pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 20)
    private MetodoPago metodoPago;
    
    /**
     * Fecha y hora en que se realizó el pago. Se asigna automáticamente al crear la entidad.
     */
    @Column(name = "fecha_pago", nullable = false, updatable = false)
    private LocalDateTime fechaPago = LocalDateTime.now();
    
    /**
     * Estado actual del pago (ej: PENDIENTE, COMPLETADO, FALLIDO, REEMBOLSADO).
     * Por defecto es PENDIENTE.
     */
    @NotNull(message = "El estado del pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPago estado = EstadoPago.PENDIENTE;
    
    /**
     * Ticket asociado a este pago. Relación uno a uno, obligatorio.
     */
    @NotNull(message = "El ticket es obligatorio")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id", nullable = false, unique = true)
    private Ticket ticket;
    
    /**
     * Usuario que realizó el pago. Relación muchos a uno, obligatorio.
     */
    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    private User usuario;
    
    /**
     * Inicializa valores por defecto antes de persistir la entidad en la base de datos.
     */
    @PrePersist
    protected void onCreate() {
        if (fechaPago == null) {
            fechaPago = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoPago.PENDIENTE;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment payment)) return false;
        return getId() != null && getId().equals(payment.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    // Enums moved to separate files in the enums package
}
