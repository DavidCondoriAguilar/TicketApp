package com.tickets.ravetix.dto.eventhistory;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

/**
 * DTO for creating or updating an event history.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventHistoryRequestDTO {
    
    @NotNull(message = "La asistencia es obligatoria")
    private Boolean asistenciaConfirmada;
    
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer calificacion;
    
    @Size(max = 1000, message = "El comentario no puede exceder los 1000 caracteres")
    private String comentario;
    
    @NotNull(message = "El ID del evento es obligatorio")
    private UUID eventoId;
    
    @NotNull(message = "El ID del usuario es obligatorio")
    private UUID usuarioId;
}
