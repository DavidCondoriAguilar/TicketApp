package com.tickets.ravetix.dto.event;

import com.tickets.ravetix.entity.Location;
import com.tickets.ravetix.enums.CategoriaEvento;
import com.tickets.ravetix.validation.FutureAfterNow;
import com.tickets.ravetix.validation.ValidEventDateRange;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for creating a new Event.
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@ValidEventDateRange
public class EventCreateDTO extends EventBaseDTO {
    
    @NotNull(message = "El ID del organizador es obligatorio")
    private Long organizadorId;
    
    // Additional fields specific to event creation can be added here
    
    @Override
    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        super.setFechaHoraInicio(fechaHoraInicio);
        updateDuracionHoras();
    }
    
    @Override
    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        if (fechaHoraFin != null && this.getFechaHoraInicio() != null && 
            !fechaHoraFin.isAfter(this.getFechaHoraInicio())) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
        }
        super.setFechaHoraFin(fechaHoraFin);
        updateDuracionHoras();
    }
    
    private void updateDuracionHoras() {
        if (this.getFechaHoraInicio() != null && this.getFechaHoraFin() != null) {
            long hours = java.time.Duration.between(this.getFechaHoraInicio(), this.getFechaHoraFin()).toHours();
            if (hours > 0) {
                this.setDuracionHoras((int) hours);
            }
        }
    }
}
