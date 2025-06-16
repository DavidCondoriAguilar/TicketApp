package com.tickets.ravetix.dto.ticket;

import com.tickets.ravetix.dto.BaseDTO;
import com.tickets.ravetix.dto.event.EventSimpleDTO;
import com.tickets.ravetix.dto.zone.ZoneSimpleDTO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Simplified DTO for ticket references.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketSimpleDTO extends BaseDTO {
    private EventSimpleDTO evento;
    private ZoneSimpleDTO zona;
    private BigDecimal precio;
    private LocalDateTime fechaCompra;
    private Boolean pagado;
}
