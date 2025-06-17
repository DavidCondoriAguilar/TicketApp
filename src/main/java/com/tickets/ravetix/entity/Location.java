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
    
    /**
     * Dirección física detallada del lugar del evento.
     * Campo obligatorio, máximo 255 caracteres.
     */
    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 255, message = "La dirección no puede tener más de 255 caracteres")
    private String direccion;
    
    /**
     * Ciudad donde se localiza el evento.
     * Campo obligatorio, máximo 100 caracteres.
     */
    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 100, message = "La ciudad no puede tener más de 100 caracteres")
    private String ciudad;
    
    /**
     * País donde se realiza el evento.
     * Campo obligatorio, máximo 100 caracteres.
     */
    @NotBlank(message = "El país es obligatorio")
    @Size(max = 100, message = "El país no puede tener más de 100 caracteres")
    private String pais;
    
    /**
     * Código postal de la ubicación (opcional).
     * Máximo 20 caracteres.
     */
    @Size(max = 20, message = "El código postal no puede tener más de 20 caracteres")
    private String codigoPostal;
    
    /**
     * Coordenada de latitud geográfica (opcional).
     */
    private Double latitud;
    
    /**
     * Coordenada de longitud geográfica (opcional).
     */
    private Double longitud;
    
    /**
     * Constructor simplificado para inicializar dirección, ciudad y país.
     * Útil para casos donde no se requiere información geográfica completa.
     */
    public Location(String direccion, String ciudad, String pais) {
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.pais = pais;
    }
    
    /**
     * Devuelve la ubicación completa en formato legible, incluyendo código postal si está presente.
     *
     * @return Cadena formateada con dirección, ciudad, país y código postal.
     */
    public String getUbicacionCompleta() {
        return String.format("%s, %s, %s", direccion, ciudad, pais) +
               (codigoPostal != null ? ", " + codigoPostal : "");
    }
}
