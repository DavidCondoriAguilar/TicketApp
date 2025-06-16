package com.tickets.ravetix.dto.zone;

import com.tickets.ravetix.dto.BaseDTO;
import com.tickets.ravetix.dto.ticket.TicketResponseDTO;
import com.tickets.ravetix.enums.TipoZona;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for zone responses.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZoneResponseDTO extends BaseDTO {
    private String nombre;
    private Integer capacidad;
    private BigDecimal precioBase;
    private TipoZona tipo;
    private Integer entradasVendidas;
    private Integer entradasDisponibles;
    
    @Builder.Default
    private List<String> beneficios = new ArrayList<>();
    
    @Builder.Default
    private List<TicketResponseDTO> tickets = new ArrayList<>();
    
    // Explicit getter for capacidad
    public Integer getCapacidad() {
        return this.capacidad;
    }
    
    // Explicit getter for tickets
    public List<TicketResponseDTO> getTickets() {
        return this.tickets;
    }
    
    // Helper methods
    public void addBeneficio(String beneficio) {
        if (beneficios == null) {
            beneficios = new ArrayList<>();
        }
        beneficios.add(beneficio);
    }
    
    public void addTicket(TicketResponseDTO ticket) {
        if (tickets == null) {
            tickets = new ArrayList<>();
        }
        tickets.add(ticket);
    }
    
    // Calculated fields
    public Integer getEntradasVendidas() {
        return tickets != null ? tickets.size() : 0;
    }
    
    public Integer getEntradasDisponibles() {
        return capacidad - getEntradasVendidas();
    }
}
