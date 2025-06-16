package com.tickets.ravetix.dto.mapper;

/**
 * Base mapper interface for DTO and entity conversion.
 *
 * @param <E> the entity type
 * @param <C> the create DTO type
 * @param <U> the update DTO type
 * @param <R> the response DTO type
 */
public interface BaseMapper<E, C, U, R> {
    
    /**
     * Converts a create DTO to an entity.
     */
    E toEntity(C dto);
    
    /**
     * Converts an entity to a response DTO.
     */
    R toDto(E entity);
    
    /**
     * Updates an entity with values from an update DTO.
     */
    void updateEntity(U dto, E entity);
}
