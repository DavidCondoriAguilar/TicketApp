package com.tickets.ravetix.dto.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String correo;
    private String password;
}