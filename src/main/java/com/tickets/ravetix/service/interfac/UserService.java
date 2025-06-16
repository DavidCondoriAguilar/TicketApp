package com.tickets.ravetix.service.interfac;

import com.tickets.ravetix.dto.user.UserCreateDTO;
import com.tickets.ravetix.dto.user.UserResponseDTO;
import com.tickets.ravetix.dto.user.UserUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for managing users.
 */
public interface UserService {
    
    /**
     * Create a new user.
     * @param userDTO the user to create
     * @return the created user
     */
    UserResponseDTO createUser(UserCreateDTO userDTO);
    
    /**
     * Get a user by ID.
     * @param id the ID of the user to retrieve
     * @return the user
     */
    UserResponseDTO getUserById(UUID id);
    
    /**
     * Get all users with pagination.
     * @param pageable the pagination information
     * @return a page of users
     */
    Page<UserResponseDTO> getAllUsers(Pageable pageable);
    
    /**
     * Update an existing user.
     * @param id the ID of the user to update
     * @param userDTO the updated user data
     * @return the updated user
     */
    UserResponseDTO updateUser(UUID id, UserUpdateDTO userDTO);
    
    /**
     * Delete a user by ID.
     * @param id the ID of the user to delete
     */
    void deleteUser(UUID id);
    
    /**
     * Check if a user with the given email exists.
     * @param email the email to check
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if a user with the given phone number exists.
     * @param phone the phone number to check
     * @return true if a user with the phone number exists, false otherwise
     */
    boolean existsByPhone(String phone);
}
