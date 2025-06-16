package com.tickets.ravetix.dto.payment;

import com.tickets.ravetix.enums.EstadoPago;
import com.tickets.ravetix.enums.MetodoPago;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for creating or updating a payment.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDTO {
    
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El monto debe tener máximo 10 dígitos enteros y 2 decimales")
    private BigDecimal monto;
    
    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPago metodoPago;
    
    @NotNull(message = "El ID del ticket es obligatorio")
    private UUID ticketId;
    
    @NotNull(message = "El ID del usuario es obligatorio")
    private UUID usuarioId;
    
    private EstadoPago estado;
    
    // Campos específicos para tarjetas
    private String numeroTarjeta;
    private String nombreTitular;
    private String fechaVencimiento;
    private String cvv;
}
