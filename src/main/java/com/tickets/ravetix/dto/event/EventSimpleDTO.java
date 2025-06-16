package com.tickets.ravetix.dto.event;

import com.tickets.ravetix.dto.BaseDTO;
import com.tickets.ravetix.enums.EstadoEvento;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Simplified DTO for event references.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventSimpleDTO extends BaseDTO {
    private String nombre;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private String ubicacion;
    private EstadoEvento estado;
}
