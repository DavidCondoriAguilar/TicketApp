package com.tickets.ravetix.dto.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for creating a new payment
 */
@Data
public class PaymentCreateDTO {
    @NotNull(message = "El ID del ticket es obligatorio")
    private Long ticketId;
    
    @NotNull(message = "El ID del método de pago es obligatorio")
    private Long metodoPagoId;
    
    @DecimalMin(value = "0.0", message = "La comisión no puede ser negativa")
    private BigDecimal comision;
    
    private String referenciaPago;
    private String notas;
}
