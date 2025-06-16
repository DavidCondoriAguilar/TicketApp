package com.tickets.ravetix.dto.zone;

import com.tickets.ravetix.dto.BaseDTO;
import com.tickets.ravetix.enums.TipoZona;
import lombok.*;

import java.math.BigDecimal;

/**
 * Simplified DTO for zone references.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZoneSimpleDTO extends BaseDTO {
    private String nombre;
    private TipoZona tipo;
    private BigDecimal precioBase;
}
