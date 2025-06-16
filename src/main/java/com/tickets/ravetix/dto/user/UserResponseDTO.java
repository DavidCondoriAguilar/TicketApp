package com.tickets.ravetix.dto.user;

import com.tickets.ravetix.dto.BaseDTO;
import lombok.experimental.SuperBuilder;
import com.tickets.ravetix.dto.eventhistory.EventHistoryResponseDTO;
import com.tickets.ravetix.dto.ticket.TicketResponseDTO;
import com.tickets.ravetix.dto.payment.PaymentResponseDTO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for user responses.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class UserResponseDTO extends BaseDTO {
    private String nombre;
    private String correo;
    private String telefono;
    
    @Builder.Default
    private List<TicketResponseDTO> tickets = new ArrayList<>();
    
    @Builder.Default
    private List<PaymentResponseDTO> pagos = new ArrayList<>();
    
    @Builder.Default
    private List<EventHistoryResponseDTO> historialEventos = new ArrayList<>();
    
    // Add helper methods for adding to collections
    public void addTicket(TicketResponseDTO ticket) {
        if (tickets == null) {
            tickets = new ArrayList<>();
        }
        tickets.add(ticket);
    }
    
    public void addPago(PaymentResponseDTO pago) {
        if (pagos == null) {
            pagos = new ArrayList<>();
        }
        pagos.add(pago);
    }
    
    public void addEventoHistorial(EventHistoryResponseDTO historial) {
        if (historialEventos == null) {
            historialEventos = new ArrayList<>();
        }
        historialEventos.add(historial);
    }
}
