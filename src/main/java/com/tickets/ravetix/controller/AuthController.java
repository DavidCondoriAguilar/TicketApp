package com.tickets.ravetix.controller;

import com.tickets.ravetix.dto.auth.JwtResponse;
import com.tickets.ravetix.dto.auth.LoginRequest;
import com.tickets.ravetix.dto.auth.RegisterRequest;
import com.tickets.ravetix.dto.auth.RegisterResponse;
import com.tickets.ravetix.entity.User;
import com.tickets.ravetix.security.jwt.JwtTokenProvider;
import com.tickets.ravetix.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Intento de login para el usuario: {}", loginRequest.getCorreo());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getCorreo(),
                            loginRequest.getPassword()
                    )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(userDetails.getUsername());
            
            log.info("Login exitoso para el usuario: {}", loginRequest.getCorreo());
            return ResponseEntity.ok(new JwtResponse(
                token,
                "Bearer",
                userDetails.getUsername(),
                userDetails.getAuthorities()
            ));
        } catch (BadCredentialsException e) {
            log.warn("Credenciales inválidas para el usuario: {}", loginRequest.getCorreo());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas");
        } catch (Exception e) {
            log.error("Error durante el login para el usuario: " + loginRequest.getCorreo(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error en la autenticación: " + e.getMessage());
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Registrando nuevo usuario: {}", registerRequest.getCorreo());
        try {
            User user = authService.register(registerRequest);
            log.info("Usuario registrado exitosamente con ID: {}", user.getId());
            
            // Autenticar al usuario después del registro
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    registerRequest.getCorreo(),
                    registerRequest.getPassword()
                )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(userDetails.getUsername());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterResponse(
                user.getId(),
                user.getNombre(),
                user.getCorreo(),
                "Usuario registrado exitosamente",
                token,
                "Bearer"
            ));
        } catch (Exception e) {
            log.error("Error al registrar usuario: " + registerRequest.getCorreo(), e);
            return ResponseEntity.badRequest()
                    .body("Error al registrar el usuario: " + e.getMessage());
        }
    }
}