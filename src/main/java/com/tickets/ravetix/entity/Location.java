package com.tickets.ravetix.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa una ubicación física donde se llevará a cabo un evento.
 * Esta clase es incrustable en otras entidades.
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    
    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 255, message = "La dirección no puede tener más de 255 caracteres")
    private String direccion;
    
    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 100, message = "La ciudad no puede tener más de 100 caracteres")
    private String ciudad;
    
    @NotBlank(message = "El país es obligatorio")
    @Size(max = 100, message = "El país no puede tener más de 100 caracteres")
    private String pais;
    
    @Size(max = 20, message = "El código postal no puede tener más de 20 caracteres")
    private String codigoPostal;
    
    private Double latitud;
    private Double longitud;
    
    /**
     * Constructor simplificado para casos comunes
     */
    public Location(String direccion, String ciudad, String pais) {
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.pais = pais;
    }
    
    /**
     * Devuelve la ubicación completa formateada
     */
    public String getUbicacionCompleta() {
        return String.format("%s, %s, %s", direccion, ciudad, pais) +
               (codigoPostal != null ? ", " + codigoPostal : "");
    }
}
