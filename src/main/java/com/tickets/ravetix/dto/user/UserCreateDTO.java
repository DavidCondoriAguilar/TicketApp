package com.tickets.ravetix.dto.user;

import com.tickets.ravetix.validation.Password;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for creating a new user.
 */
@Getter
@Setter
public class UserCreateDTO extends UserBaseDTO {
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Password
    private String password;
    
    @NotBlank(message = "La confirmación de contraseña es obligatoria")
    private String confirmPassword;
    
    @NotNull(message = "Debe aceptar los términos y condiciones")
    private Boolean terminosAceptados;
    
    /**
     * Valida que la contraseña y su confirmación coincidan.
     * @return true si las contraseñas coinciden, false en caso contrario
     */
    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }
}
