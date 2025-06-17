package com.tickets.ravetix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

/**
 * Represents a user's history with an event.
 * <p>
 * Tracks user attendance, ratings, and comments for past events.
 * </p>
 */
@Entity
@Table(name = "event_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@ToString(exclude = {"usuario", "evento"})
public class EventHistory extends BaseEntity {
    
    /**
     * Usuario asociado a este historial de evento.
     * Relación muchos a uno, obligatorio.
     */
    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    private User usuario;
    
    /**
     * Evento asociado a este historial.
     * Relación muchos a uno, obligatorio.
     */
    @NotNull(message = "El evento es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false, updatable = false)
    private Event evento;
    
    /**
     * Indica si la asistencia del usuario al evento fue confirmada.
     */
    @Column(name = "asistencia_confirmada", nullable = false)
    private boolean asistenciaConfirmada = false;

    /**
     * Calificación otorgada por el usuario al evento (rango 1-5).
     */
    @Min(1) @Max(5)
    @Column
    private Integer calificacion;
    
    /**
     * Comentario opcional del usuario sobre el evento.
     */
    @Column(columnDefinition = "TEXT")
    private String comentario;
    
    /**
     * Fecha y hora en que se confirmó la asistencia.
     */
    @Column(name = "fecha_confirmacion_asistencia")
    private LocalDateTime fechaConfirmacionAsistencia;
    
    /**
     * Fecha y hora en que se realizó la calificación del evento.
     */
    @Column(name = "fecha_calificacion")
    private LocalDateTime fechaCalificacion;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventHistory that)) return false;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
