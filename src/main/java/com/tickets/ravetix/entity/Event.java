package com.tickets.ravetix.entity;

import com.tickets.ravetix.enums.EstadoEvento;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an event in the system.
 * <p>
 * Contains all information about an event including its schedule, location,
 * pricing, and related entities like tickets and zones.
 * </p>
 */
@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@DynamicUpdate
@ToString(callSuper = true, exclude = {"tickets", "zonas", "historial"})
@EqualsAndHashCode(callSuper = true)
public class Event extends BaseEntity {
    
    // ID heredado de BaseEntity
    
    @NotBlank(message = "El nombre del evento es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private EstadoEvento estado;

    @Future(message = "La fecha de inicio debe ser futura")
    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;
    
    @Future(message = "La fecha de fin debe ser futura")
    @Column(name = "fecha_hora_fin", nullable = false)
    private LocalDateTime fechaHoraFin;
    
    @Embedded
    @NotNull(message = "La ubicación es obligatoria")
    private Location ubicacion;

    @Column(name = "duracion_horas", nullable = false)
    private Integer duracionHoras;
    
    @PrePersist
    @PreUpdate
    private void calculateDuracionHoras() {
        if (fechaHoraInicio != null && fechaHoraFin != null) {
            long hours = java.time.Duration.between(fechaHoraInicio, fechaHoraFin).toHours();
            this.duracionHoras = (int) Math.max(1, hours);
        } else if (this.duracionHoras == null) {
            this.duracionHoras = 1; // default value
        }
    }
    
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();
    
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Zone> zonas = new ArrayList<>();
    
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventHistory> historial = new ArrayList<>();
    

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event event)) return false;
        return getId() != null && getId().equals(event.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
