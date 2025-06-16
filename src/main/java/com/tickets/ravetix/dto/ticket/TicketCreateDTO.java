package com.tickets.ravetix.dto.ticket;

import com.tickets.ravetix.enums.MetodoPago;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * DTO for creating a new Ticket
 */
@Getter
@Setter
public class TicketCreateDTO extends TicketBaseDTO {
    @NotNull(message = "El ID del comprador es obligatorio")
    private UUID compradorId;
    
    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPago metodoPago;
}
