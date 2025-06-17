package com.tickets.ravetix.entity;

import com.tickets.ravetix.enums.TicketState;
import com.tickets.ravetix.enums.TipoZona;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a zone within an event.
 * <p>
 * Defines different areas in an event with specific capacity, pricing, and benefits.
 * Each zone can have multiple tickets associated with it.
 * </p>
 */
@Entity
@Table(name = "zones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@ToString(exclude = {"evento", "tickets"})
public class Zone extends BaseEntity {
    
    /**
     * Nombre identificador de la zona dentro del evento.
     * Debe tener entre 2 y 50 caracteres.
     */
    @NotBlank(message = "El nombre de la zona es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Column(nullable = false, length = 50)
    private String nombre;

    /**
     * Capacidad máxima de personas que puede albergar la zona.
     * Debe ser al menos 1.
     */
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer capacidad;
    
    /**
     * Precio base de los tickets en esta zona.
     * Debe ser mayor a 0 y tener hasta 10 dígitos enteros y 2 decimales.
     */
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio base debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio base debe tener máximo 10 dígitos enteros y 2 decimales")
    @Column(name = "precio_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBase;
    
    /**
     * Lista de beneficios incluidos en la zona (ej: acceso VIP, bebidas, etc).
     */
    @ElementCollection
    @CollectionTable(name = "zone_beneficios", joinColumns = @JoinColumn(name = "zone_id"))
    @Column(name = "beneficio")
    private List<String> beneficios = new ArrayList<>();
    
    /**
     * Evento al que pertenece esta zona. Relación muchos a uno, obligatorio.
     */
    @NotNull(message = "El evento es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false, updatable = false)
    private Event evento;

    /**
     * Tipo de zona (ej: GENERAL, VIP, PLATINUM, etc).
     */
    @NotNull(message = "El tipo de zona es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoZona tipo;

    /**
     * Número de entradas vendidas en la zona.
     */
    @Column(name = "entradas_vendidas", nullable = false)
    private Integer entradasVendidas = 0;

    /**
     * Número de entradas disponibles en la zona.
     */
    @Column(name = "entradas_disponibles", nullable = false)
    private Integer entradasDisponibles = 0;

    /**
     * Actualiza las estadísticas de entradas cuando se crea o actualiza la zona.
     */
    @PrePersist
    @PreUpdate
    private void updateStatistics() {
        if (tickets != null) {
            this.entradasVendidas = (int) tickets.stream()
                .filter(ticket -> ticket.getEstado() == TicketState.PAGADO)
                .count();
            this.entradasDisponibles = Math.max(0, this.capacidad - this.entradasVendidas);
        }
    }
    
    /**
     * Tickets asociados a esta zona. Relación uno a muchos.
     */
    @OneToMany(mappedBy = "zona", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Zone zone)) return false;
        return getId() != null && getId().equals(zone.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
