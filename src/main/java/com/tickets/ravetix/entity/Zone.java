package com.tickets.ravetix.entity;

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
    
    @NotBlank(message = "El nombre de la zona es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Column(nullable = false, length = 50)
    private String nombre;

    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer capacidad;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio base debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio base debe tener máximo 10 dígitos enteros y 2 decimales")
    @Column(name = "precio_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBase;
    
    @ElementCollection
    @CollectionTable(name = "zone_beneficios", joinColumns = @JoinColumn(name = "zone_id"))
    @Column(name = "beneficio")
    private List<String> beneficios = new ArrayList<>();
    
    @NotNull(message = "El evento es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false, updatable = false)
    private Event evento;

    @NotNull(message = "El tipo de zona es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoZona tipo;
    
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
