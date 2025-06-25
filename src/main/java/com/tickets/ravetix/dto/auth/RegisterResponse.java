package com.tickets.ravetix.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO para la respuesta de registro de usuario.
 * Contiene la información del usuario registrado y el token de autenticación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private UUID id;
    private String nombre;
    private String correo;
    private String mensaje;
    private String token;
    private String tokenType;
}
