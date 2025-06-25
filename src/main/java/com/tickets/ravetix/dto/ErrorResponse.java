package com.tickets.ravetix.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String timestamp;  // Cambiado a String
    private int status;
    private String error;
    private String message;
    private String path;

    // Método estático para crear la respuesta
    public static ErrorResponse of(int status, String error, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .build();
    }
}