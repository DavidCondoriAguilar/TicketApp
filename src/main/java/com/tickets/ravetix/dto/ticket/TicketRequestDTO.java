package com.tickets.ravetix.dto.ticket;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for creating or updating a ticket.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketRequestDTO {
    
    @NotNull(message = "El ID del evento es obligatorio")
    private UUID eventoId;
    
    @NotNull(message = "El ID de la zona es obligatorio")
    private UUID zonaId;
    
    @NotNull(message = "El ID del comprador es obligatorio")
    private UUID compradorId;
    
    @NotNull(message = "El precio es obligatorio")
    private BigDecimal precio;
    
    @NotNull(message = "El método de pago es obligatorio")
    private String metodoPago;
    
    @JsonCreator
    public static TicketRequestDTO create(
            @JsonProperty("eventoId") Object eventoId,
            @JsonProperty("zonaId") Object zonaId,
            @JsonProperty("compradorId") Object compradorId,
            @JsonProperty("precio") BigDecimal precio,
            @JsonProperty("metodoPago") String metodoPago) {
        
        TicketRequestDTO dto = new TicketRequestDTO();
        dto.setEventoId(convertToUUID(eventoId, "eventoId"));
        dto.setZonaId(convertToUUID(zonaId, "zonaId"));
        dto.setCompradorId(convertToUUID(compradorId, "compradorId"));
        dto.setPrecio(precio);
        dto.setMetodoPago(metodoPago);
        return dto;
    }
    
    private static UUID convertToUUID(Object value, String fieldName) {
        if (value == null) {
            return null;
        }
        
        try {
            if (value instanceof UUID) {
                return (UUID) value;
            } else if (value instanceof String) {
                String strValue = ((String) value).trim();
                // If it's a numeric string, we'll assume it's a numeric ID
                if (strValue.matches("\\d+")) {
                    // Convert numeric ID to UUID
                    String uuidString = String.format("00000000-0000-0000-0000-%012d", Long.parseLong(strValue));
                    return UUID.fromString(uuidString);
                }
                return UUID.fromString(strValue);
            } else if (value instanceof Number) {
                // Handle case where the value is a number (from JSON number)
                String uuidString = String.format("00000000-0000-0000-0000-%012d", ((Number) value).longValue());
                return UUID.fromString(uuidString);
            }
            throw new IllegalArgumentException("Invalid UUID format for field: " + fieldName);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid UUID format for field " + fieldName + ": " + value, e);
        }
    }
    
    // Standard setters that accept UUID
    public void setEventoId(UUID eventoId) {
        this.eventoId = eventoId;
    }
    
    public void setZonaId(UUID zonaId) {
        this.zonaId = zonaId;
    }
    
    public void setCompradorId(UUID compradorId) {
        this.compradorId = compradorId;
    }
}
