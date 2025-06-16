package com.tickets.ravetix.dto.zone;

import com.tickets.ravetix.enums.TipoZona;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZoneCreateDTO {
    @NotBlank(message = "El nombre de la zona es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    private String descripcion;

    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    private Integer capacidad;

    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precioBase;

    @NotNull(message = "El tipo de zona es obligatorio")
    private TipoZona tipo;

    @Builder.Default
    private List<String> beneficios = new ArrayList<>();

    @NotNull(message = "El ID del evento es obligatorio")
    private UUID eventoId;
}