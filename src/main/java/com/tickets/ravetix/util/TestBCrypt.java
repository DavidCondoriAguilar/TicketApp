package com.tickets.ravetix.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 1. Generar un hash NUEVO
        String rawPassword = "password123";
        String newHash = encoder.encode(rawPassword);
        System.out.println("Nuevo hash generado: " + newHash);

        // 2. Verificar la contraseña con el NUEVO hash
        boolean matches = encoder.matches(rawPassword, newHash);
        System.out.println("Verificación con NUEVO hash: " + matches);

        // 3. Verificar con el hash que tienes en la base de datos
        String storedHash = "$2a$10$XURPShQNCsLjp1ESc2laoObo9QZDhxz73hJPaEv7/cBha4pk0AgP.";
        boolean matchesStored = encoder.matches(rawPassword, storedHash);
        System.out.println("Verificación con hash ALMACENADO: " + matchesStored);
    }
}