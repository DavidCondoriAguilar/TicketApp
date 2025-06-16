package com.tickets.ravetix.dto.user;

import com.tickets.ravetix.validation.ValidPhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Base DTO for user operations containing common fields and validations.
 */
@Data
public abstract class UserBaseDTO {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    protected String nombre;
    
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede tener más de 100 caracteres")
    protected String apellido;
    
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo electrónico no es válido")
    protected String correo;
    
    @ValidPhoneNumber
    protected String telefono;
    
    protected String direccion;
    
    @Pattern(regexp = "^[A-Z]{2,4}-?\\.?\\s?[0-9]{4}-?[0-9]{4}$", 
             message = "El formato del DUI no es válido (ej: 12345678-9 o 123456789)")
    protected String dui;
    
    protected String fotoPerfilUrl;
}
