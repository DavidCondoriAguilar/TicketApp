package com.tickets.ravetix.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Clase que representa la respuesta de autenticación JWT.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type;
    private String username;
    private Collection<? extends GrantedAuthority> authorities;
    
    /**
     * Constructor para respuestas simples con solo el token.
     * Establece el tipo de token como "Bearer" por defecto.
     * 
     * @param token El token JWT
     */
    public JwtResponse(String token) {
        this.token = token;
        this.type = "Bearer";
    }
}