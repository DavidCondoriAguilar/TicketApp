package com.tickets.ravetix.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Base service interface with common CRUD operations.
 *
 * @param <T>  the entity type
 * @param <ID> the type of the entity's identifier
 * @param <C>  the create DTO type
 * @param <U>  the update DTO type
 * @param <R>  the response DTO type
 */
public interface BaseService<T, ID, C, U, R> {
    
    /**
     * Find all entities with pagination.
     */
    Page<R> findAll(Pageable pageable);
    
    /**
     * Find an entity by ID.
     */
    R findById(ID id);
    
    /**
     * Create a new entity.
     */
    R create(C createDto);
    
    /**
     * Update an existing entity.
     */
    R update(ID id, U updateDto);
    
    /**
     * Delete an entity by ID.
     */
    void delete(ID id);
}
