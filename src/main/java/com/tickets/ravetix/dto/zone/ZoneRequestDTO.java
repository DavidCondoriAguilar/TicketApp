package com.tickets.ravetix.dto.zone;

import com.tickets.ravetix.enums.TipoZona;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for creating or updating a zone.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZoneRequestDTO {
    
    @NotBlank(message = "El nombre de la zona es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;
    
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    private Integer capacidad;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio base debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio base debe tener máximo 10 dígitos enteros y 2 decimales")
    private BigDecimal precioBase;
    
    @NotNull(message = "El tipo de zona es obligatorio")
    private TipoZona tipo;
    
    @Builder.Default
    private List<String> beneficios = new ArrayList<>();
    
    // Helper methods
    public void addBeneficio(String beneficio) {
        if (beneficios == null) {
            beneficios = new ArrayList<>();
        }
        beneficios.add(beneficio);
    }
}
