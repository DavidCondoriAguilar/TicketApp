package com.tickets.ravetix.dto.auth;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private String password;
    private String confirmPassword;
    private boolean terminosAceptados;
}
