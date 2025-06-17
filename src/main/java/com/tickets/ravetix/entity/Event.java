package com.tickets.ravetix.entity;

import com.tickets.ravetix.enums.EstadoEvento;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.time.Duration;
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
    
    /**
     * Nombre del evento. Debe ser único y tener entre 3 y 100 caracteres.
     */
    @NotBlank(message = "El nombre del evento es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;
    
    /**
     * Descripción detallada del evento.
     */
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /**
     * Estado actual del evento (ej: CREADO, ACTIVO, FINALIZADO, CANCELADO).
     */
    @Enumerated(EnumType.STRING)
    private EstadoEvento estado;

    /**
     * Fecha y hora de inicio del evento. Debe ser una fecha futura.
     */
    @Future(message = "La fecha de inicio debe ser futura")
    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;
    
    /**
     * Fecha y hora de fin del evento. Debe ser una fecha futura.
     */
    @Future(message = "La fecha de fin debe ser futura")
    @Column(name = "fecha_hora_fin", nullable = false)
    private LocalDateTime fechaHoraFin;

    @Column(name = "duracion_horas")
    private Integer duracionHoras;
    
    /**
     * Ubicación física donde se realiza el evento.
     */
    @Embedded
    @NotNull(message = "La ubicación es obligatoria")
    private Location ubicacion;

    @Column(name = "capacidad_total")
    private Integer capacidadTotal;

    @Column(name = "entradas_vendidas")
    private Integer entradasVendidas;

    @Column(name = "entradas_disponibles")
    private Integer entradasDisponibles;

    /**
     * Calcula la duración del evento en horas antes de persistir o actualizar.
     */
    @PreUpdate
    private void calculateDuracionHoras() {
        if (fechaHoraInicio != null && fechaHoraFin != null) {
            long hours = Duration.between(fechaHoraInicio, fechaHoraFin).toHours();
            this.duracionHoras = (int) Math.max(1, hours);
        } else if (this.duracionHoras == null) {
            this.duracionHoras = 1; // default value
        }
    }

    /**
     * Calcula la capacidad total del evento sumando las capacidades de todas las zonas.
     */
    @PrePersist
    private void calculateCapacidadTotal() {
        if (zonas != null) {
            this.capacidadTotal = zonas.stream()
                .mapToInt(Zone::getCapacidad)
                .sum();
        }
    }

    /**
     * Lista de tickets asociados a este evento.
     */
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();
    
    /**
     * Lista de zonas disponibles en el evento.
     */
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Zone> zonas = new ArrayList<>();
    
    /**
     * Historial de acciones y asistencias relacionadas con el evento.
     */
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
