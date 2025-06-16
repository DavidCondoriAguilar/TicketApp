package com.tickets.ravetix.dto.event;

import com.tickets.ravetix.entity.Location;
import com.tickets.ravetix.enums.CategoriaEvento;
import com.tickets.ravetix.enums.EstadoEvento;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Base DTO for Event operations.
 * Contains common fields for create and update operations.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public abstract class EventBaseDTO {
    
    @NotBlank(message = "El nombre del evento es obligatorio")
    @Size(max = 255, message = "El nombre no puede tener más de 255 caracteres")
    protected String nombre;
    
    @Size(max = 1000, message = "La descripción no puede tener más de 1000 caracteres")
    protected String descripcion;
    
    @NotNull(message = "La fecha y hora de inicio son obligatorias")
    @Future(message = "La fecha de inicio debe ser en el futuro")
    protected LocalDateTime fechaHoraInicio;
    
    @NotNull(message = "La fecha y hora de fin son obligatorias")
    @Future(message = "La fecha de fin debe ser en el futuro")
    protected LocalDateTime fechaHoraFin;
    
    @NotNull(message = "La ubicación es obligatoria")
    protected Location ubicacion;
    
    @NotNull(message = "La categoría es obligatoria")
    protected CategoriaEvento categoria;
    
    @Min(value = 1, message = "La duración debe ser al menos 1 hora")
    @Max(value = 72, message = "La duración no puede exceder las 72 horas")
    @Builder.Default
    protected Integer duracionHoras = 1;
    
    @Min(value = 0, message = "La capacidad no puede ser negativa")
    protected Integer capacidadMaxima;
    
    @Min(value = 0, message = "La edad mínima no puede ser negativa")
    protected Integer edadMinima;
    
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    protected BigDecimal precioBase;

    protected String terminosCondiciones;
}
