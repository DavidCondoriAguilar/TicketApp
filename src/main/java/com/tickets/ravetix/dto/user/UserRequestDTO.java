package com.tickets.ravetix.dto.user;

import com.tickets.ravetix.dto.BaseDTO;
import com.tickets.ravetix.validation.ValidPhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for creating or updating a user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDTO extends BaseDTO {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;
    
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El formato del correo no es válido")
    private String correo;
    
    @NotBlank(message = "El teléfono es obligatorio")
    @ValidPhoneNumber
    private String telefono;
    
    // Password fields would be added here for registration
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
    
    @Size(min = 8, message = "La confirmación de contraseña es obligatoria")
    private String confirmPassword;
}
