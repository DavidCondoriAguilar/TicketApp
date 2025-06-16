package com.tickets.ravetix.dto.event;

import com.tickets.ravetix.entity.Location;
import com.tickets.ravetix.enums.CategoriaEvento;
import com.tickets.ravetix.enums.EstadoEvento;
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
 * DTO for updating an existing Event.
 * All fields are optional for partial updates.
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@ValidEventDateRange
public class EventUpdateDTO extends EventBaseDTO {
    
    // Override parent fields to make them optional for updates
    @Override
    public String getNombre() {
        return super.getNombre();
    }
    
    @Override
    public String getDescripcion() {
        return super.getDescripcion();
    }
    
    @FutureAfterNow(message = "La fecha de inicio debe ser en el futuro")
    @Override
    public LocalDateTime getFechaHoraInicio() {
        return super.getFechaHoraInicio();
    }
    
    @FutureAfterNow(message = "La fecha de fin debe ser en el futuro")
    @Override
    public LocalDateTime getFechaHoraFin() {
        return super.getFechaHoraFin();
    }
    
    @Valid
    @Override
    public Location getUbicacion() {
        return super.getUbicacion();
    }
    
    @Override
    public CategoriaEvento getCategoria() {
        return super.getCategoria();
    }
    
    @Override
    public Integer getCapacidadMaxima() {
        return super.getCapacidadMaxima();
    }
    
    @Override
    public Integer getEdadMinima() {
        return super.getEdadMinima();
    }
    
    @Override
    public BigDecimal getPrecioBase() {
        return super.getPrecioBase();
    }

    @Override
    public String getTerminosCondiciones() {
        return super.getTerminosCondiciones();
    }
    
    private EstadoEvento estado;
    
    // Additional fields specific to event updates can be added here
}
