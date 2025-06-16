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
    
    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    private User usuario;
    
    @NotNull(message = "El evento es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false, updatable = false)
    private Event evento;
    
    @Column(name = "asistencia_confirmada", nullable = false)
    private boolean asistenciaConfirmada = false;

    @Min(1) @Max(5)
    @Column
    private Integer calificacion;
    
    @Column(columnDefinition = "TEXT")
    private String comentario;
    
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
