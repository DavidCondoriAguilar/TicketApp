package com.tickets.ravetix.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base entity class that provides common fields and functionality for all entities.
 * <p>
 * This abstract class serves as a foundation for all JPA entities in the application,
 * providing common fields like ID, creation timestamp, and last update timestamp.
 * It helps reduce code duplication and ensures consistency across all entities.
 * </p>
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public abstract class BaseEntity {
    
    /**
     * Unique identifier for the entity.
     * Uses UUID generation strategy for better distribution and uniqueness.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
    
    /**
     * Timestamp when the entity was created.
     * Automatically set by Hibernate when the entity is first persisted.
     */
    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false, nullable = false)
    private LocalDateTime fechaCreacion;
    
    /**
     * Timestamp when the entity was last updated.
     * Automatically updated by Hibernate whenever the entity is modified.
     */
    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    /**
     * Version field for optimistic locking.
     * Prevents concurrent modifications to the same entity.
     */
    @Version
    private Long version;
    
    /**
     * Indicates whether the entity is new (not yet persisted).
     * @return true if the entity has not been persisted yet
     */
    @Transient
    public boolean isNew() {
        return this.id == null;
    }
}
