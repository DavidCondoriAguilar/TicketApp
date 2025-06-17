package com.tickets.ravetix.service.impl;

import com.tickets.ravetix.dto.eventhistory.EventHistoryResponseDTO;
import com.tickets.ravetix.dto.mapper.UserMapper;
import com.tickets.ravetix.dto.payment.PaymentResponseDTO;
import com.tickets.ravetix.dto.user.UserCreateDTO;
import com.tickets.ravetix.dto.user.UserResponseDTO;
import com.tickets.ravetix.dto.user.UserUpdateDTO;
import com.tickets.ravetix.entity.User;
import com.tickets.ravetix.exception.NotFoundException;
import com.tickets.ravetix.repository.UserRepository;
import com.tickets.ravetix.service.interfac.EventHistoryService;
import com.tickets.ravetix.service.interfac.PaymentService;
import com.tickets.ravetix.service.interfac.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of the UserService interface.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PaymentService paymentService;
    private final EventHistoryService eventHistoryService;

    @Override
    @Transactional
    public UserResponseDTO createUser(UserCreateDTO userDTO) {
        // Check if email already exists
        if (userRepository.existsByCorreo(userDTO.getCorreo())) {
            throw new IllegalStateException("Email already in use");
        }
        
        // Check if phone already exists
        if (userRepository.existsByTelefono(userDTO.getTelefono())) {
            throw new IllegalStateException("Phone number already in use");
        }
        
        User user = userMapper.toEntity(userDTO);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(UUID id) {
        // Get user and convert to DTO
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        UserResponseDTO userDTO = userMapper.toDto(user);
        
        // Get user's payments
        Page<PaymentResponseDTO> payments = paymentService.getPaymentsByUserId(id, Pageable.unpaged());
        if (payments != null && payments.hasContent()) {
            userDTO.setPagos(payments.getContent());
        }
        
        // Get user's event history
        Page<EventHistoryResponseDTO> eventHistory = eventHistoryService.getEventHistoryByUserId(id, Pageable.unpaged());
        if (eventHistory != null && eventHistory.hasContent()) {
            userDTO.setHistorialEventos(eventHistory.getContent());
        }
        
        return userDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(UUID id, UserUpdateDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        
        // Check if new email is already in use by another user
        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(existingUser.getCorreo())) {
            if (userRepository.existsByCorreo(userDTO.getEmail())) {
                throw new IllegalStateException("Email already in use");
            }
        }
        
        // Check if new phone is already in use by another user
        if (userDTO.getTelefono() != null && !userDTO.getTelefono().equals(existingUser.getTelefono())) {
            if (userRepository.existsByTelefono(userDTO.getTelefono())) {
                throw new IllegalStateException("Phone number already in use");
            }
        }
        
        userMapper.updateEntity(userDTO, existingUser);
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByCorreo(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPhone(String phone) {
        return userRepository.existsByTelefono(phone);
    }
}
