package com.tickets.ravetix.dto.ticket;

import com.tickets.ravetix.dto.BaseDTO;
import com.tickets.ravetix.dto.event.EventSimpleDTO;
import com.tickets.ravetix.dto.user.UserSimpleDTO;
import com.tickets.ravetix.dto.zone.ZoneSimpleDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for ticket responses.
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class TicketResponseDTO extends BaseDTO {
    private EventSimpleDTO evento;
    private ZoneSimpleDTO zona;
    private UserSimpleDTO usuario;
    private BigDecimal precio;
    private LocalDateTime fechaCompra;
    private Boolean pagado;
    private String estado;
}
