package com.tickets.ravetix.repository;

import com.tickets.ravetix.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Base repository interface that provides common CRUD operations for all entities.
 *
 * @param <T>  the entity type
 * @param <ID> the type of the entity's identifier
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID extends Serializable> 
    extends JpaRepository<T, ID> {
}
