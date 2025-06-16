package com.tickets.ravetix.dto.event;

import com.tickets.ravetix.dto.BaseDTO;
import com.tickets.ravetix.dto.zone.ZoneResponseDTO;
import com.tickets.ravetix.enums.EstadoEvento;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for event responses.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class EventResponseDTO extends BaseDTO {
    private String nombre;
    private String descripcion;
    private EstadoEvento estado;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private String ubicacion;
    private Integer duracionHoras;
    private BigDecimal precioBase;
    private Integer capacidadTotal;
    private Integer entradasVendidas;
    private Integer entradasDisponibles;
    
    @Builder.Default
    private List<ZoneResponseDTO> zonas = new ArrayList<>();
    
    // Helper methods
    public void addZona(ZoneResponseDTO zona) {
        if (zonas == null) {
            zonas = new ArrayList<>();
        }
        zonas.add(zona);
    }
    
    // Calculated fields
    public Integer getCapacidadTotal() {
        if (zonas == null) {
            return 0;
        }
        return zonas.stream()
                .mapToInt(zone -> zone.getCapacidad() != null ? zone.getCapacidad() : 0)
                .sum();
    }
    
    public Integer getEntradasVendidas() {
        if (zonas == null) {
            return 0;
        }
        return zonas.stream()
                .mapToInt(zone -> {
                    if (zone == null || zone.getTickets() == null) {
                        return 0;
                    }
                    return zone.getTickets().size();
                })
                .sum();
    }
    
    public Integer getEntradasDisponibles() {
        return getCapacidadTotal() - getEntradasVendidas();
    }
}
