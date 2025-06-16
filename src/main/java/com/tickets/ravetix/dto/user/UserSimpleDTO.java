package com.tickets.ravetix.dto.user;

import com.tickets.ravetix.dto.BaseDTO;
import lombok.*;

/**
 * Simplified DTO for user references.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSimpleDTO extends BaseDTO {
    private String nombre;
    private String correo;
}
