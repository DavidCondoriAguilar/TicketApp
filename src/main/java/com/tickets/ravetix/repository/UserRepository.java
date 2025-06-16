package com.tickets.ravetix.repository;

import com.tickets.ravetix.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link User} entity.
 * Provides methods to perform database operations on users.
 */
@Repository
public interface UserRepository extends BaseRepository<User, UUID> {
    
    /**
     * Find a user by email.
     *
     * @param email the email to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByCorreo(String email);
    
    /**
     * Check if a user with the given email exists.
     *
     * @param email the email to check
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByCorreo(String email);
    
    /**
     * Check if a user with the given phone number exists.
     *
     * @param telefono the phone number to check
     * @return true if a user with the phone number exists, false otherwise
     */
    boolean existsByTelefono(String telefono);
}
