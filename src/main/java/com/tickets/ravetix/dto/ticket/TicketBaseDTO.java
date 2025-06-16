package com.tickets.ravetix.dto.ticket;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Base DTO for Ticket operations
 */
@Data
public abstract class TicketBaseDTO {
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    protected BigDecimal precio;
    
    @NotNull(message = "El ID del evento es obligatorio")
    protected UUID eventoId;
    
    @NotNull(message = "El ID de la zona es obligatorio")
    protected UUID zonaId;
    
    protected String codigoQr;
    protected String estado;
}
