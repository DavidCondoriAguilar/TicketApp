package com.tickets.ravetix.service;

import com.tickets.ravetix.dto.auth.RegisterRequest;
import com.tickets.ravetix.entity.User;
import com.tickets.ravetix.exception.DuplicateResourceException;
import com.tickets.ravetix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(RegisterRequest registerRequest) {
        log.info("Intentando registrar nuevo usuario con correo: {}", registerRequest.getCorreo());
        
        // Verificar si el correo ya existe
        if (userRepository.existsByCorreo(registerRequest.getCorreo())) {
            log.warn("Intento de registro fallido: El correo {} ya está registrado", registerRequest.getCorreo());
            throw new DuplicateResourceException("El correo electrónico ya está en uso");
        }

        // Verificar si el teléfono ya existe
        if (userRepository.existsByTelefono(registerRequest.getTelefono())) {
            log.warn("Intento de registro fallido: El teléfono {} ya está registrado", registerRequest.getTelefono());
            throw new DuplicateResourceException("El número de teléfono ya está en uso");
        }

        try {
            // Crear nuevo usuario
            User user = new User();
            user.setNombre(registerRequest.getNombre() + " " + registerRequest.getApellido());
            user.setCorreo(registerRequest.getCorreo().toLowerCase().trim());
            user.setTelefono(registerRequest.getTelefono().trim());
            
            // Encriptar contraseña
            String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
            user.setPassword(encodedPassword);

            // Guardar usuario en la base de datos
            User savedUser = userRepository.save(user);
            log.info("Usuario registrado exitosamente con ID: {}", savedUser.getId());
            
            return savedUser;
        } catch (Exception e) {
            log.error("Error al registrar el usuario: {}", e.getMessage(), e);
            throw new RuntimeException("Error al registrar el usuario: " + e.getMessage(), e);
        }
    }
}
