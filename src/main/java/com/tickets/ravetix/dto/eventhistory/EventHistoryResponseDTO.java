package com.tickets.ravetix.dto.eventhistory;

import com.tickets.ravetix.dto.BaseDTO;
import com.tickets.ravetix.dto.event.EventSimpleDTO;
import com.tickets.ravetix.dto.user.UserSimpleDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * DTO for event history responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class EventHistoryResponseDTO extends BaseDTO {
    private EventSimpleDTO evento;
    private UserSimpleDTO usuario;
    private Boolean asistenciaConfirmada;
    private Integer calificacion;
    private String comentario;
    private LocalDateTime fechaRegistro;
}
