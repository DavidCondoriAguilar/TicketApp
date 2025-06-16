package com.tickets.ravetix.dto.payment;

import com.tickets.ravetix.dto.BaseDTO;
import com.tickets.ravetix.dto.ticket.TicketSimpleDTO;
import com.tickets.ravetix.dto.user.UserSimpleDTO;
import com.tickets.ravetix.enums.EstadoPago;
import com.tickets.ravetix.enums.MetodoPago;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for payment responses.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDTO extends BaseDTO {
    private BigDecimal monto;
    private MetodoPago metodoPago;
    private EstadoPago estado;
    private LocalDateTime fechaPago;
    private TicketSimpleDTO ticket;
    private UserSimpleDTO usuario;
    private String referenciaPago;
    private String mensajeError;
}
