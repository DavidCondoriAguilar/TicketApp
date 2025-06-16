package com.tickets.ravetix.dto.event;

import com.tickets.ravetix.dto.BaseDTO;
import com.tickets.ravetix.enums.EstadoEvento;
import com.tickets.ravetix.validation.FutureAfterNow;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for creating or updating an event.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequestDTO extends BaseDTO {
    
    @NotBlank(message = "El nombre del evento es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;
    
    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    private String descripcion;
    
    private EstadoEvento estado;
    
    @FutureAfterNow(message = "La fecha de inicio debe ser futura", minutesInFuture = 30)
    @NotNull(message = "La fecha y hora de inicio son obligatorias")
    private LocalDateTime fechaHoraInicio;
    
    @FutureAfterNow(message = "La fecha de fin debe ser futura", minutesInFuture = 30)
    @NotNull(message = "La fecha y hora de fin son obligatorias")
    private LocalDateTime fechaHoraFin;
    
    @NotBlank(message = "La ubicación es obligatoria")
    @Size(max = 200, message = "La ubicación no puede exceder los 200 caracteres")
    private String ubicacion;
    
    @Min(value = 1, message = "La duración debe ser al menos 1 hora")
    @Max(value = 72, message = "La duración no puede exceder las 72 horas")
    @Builder.Default
    private Integer duracionHoras = 1;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio base debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio base debe tener máximo 10 dígitos enteros y 2 decimales")
    @Builder.Default
    private BigDecimal precioBase = BigDecimal.ZERO;
}
