package com.tickets.ravetix.dto.user;

import com.tickets.ravetix.validation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for updating an existing user.
 * All fields are optional for partial updates.
 */
@Getter
@Setter
public class UserUpdateDTO extends UserBaseDTO {
    
    /**
     * Sobrescribimos los getters para hacer los campos opcionales en actualizaciones
     */
    @Override
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    public String getNombre() {
        return super.getNombre();
    }
    
    @Override
    @Size(max = 100, message = "El apellido no puede tener más de 100 caracteres")
    public String getApellido() {
        return super.getApellido();
    }
    
    @Email(message = "El formato del correo electrónico no es válido")
    public String getEmail() {
        return super.getCorreo();
    }
    
    @Password
    private String newPassword;
    
    private String confirmNewPassword;
    
    private String currentPassword;
    
    /**
     * Verifica si se está intentando actualizar la contraseña
     */
    public boolean isUpdatingPassword() {
        return newPassword != null && !newPassword.trim().isEmpty();
    }
    
    /**
     * Valida que la nueva contraseña y su confirmación coincidan
     */
    public boolean isNewPasswordMatching() {
        return newPassword != null && newPassword.equals(confirmNewPassword);
    }
}
