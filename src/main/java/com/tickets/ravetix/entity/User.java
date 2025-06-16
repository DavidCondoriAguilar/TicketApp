package com.tickets.ravetix.entity;

import com.tickets.ravetix.validation.ValidPhoneNumber;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a user in the system.
 * <p>
 * This entity stores user information including personal details and relationships
 * to tickets, payments, and event history.
 * </p>
 */
@Entity
@Table(name = "users", 
       uniqueConstraints = @UniqueConstraint(columnNames = "correo"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@ToString(exclude = {"tickets", "pagos", "historialEventos"})
public class User extends BaseEntity {
    
    /**
     * User's full name.
     * Must not be empty and should be between 2 and 100 characters.
     */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Email
    /**
     * User's email address.
     * Must be a valid email format and unique across the system.
     */
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El formato del correo no es válido")
    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    @Size(min = 9, max = 15)
    /**
     * User's phone number.
     * Must be a valid phone number format.
     */
    @NotBlank(message = "El teléfono es obligatorio")
    @ValidPhoneNumber
    @Column(nullable = false, length = 20)
    private String telefono;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return getId() != null && getId().equals(user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> pagos = new ArrayList<>();
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventHistory> historialEventos = new ArrayList<>();
    
    /**
     * The date and time when the user was registered.
     * Automatically set when the user is first persisted.
     */
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;
    
    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }
}
