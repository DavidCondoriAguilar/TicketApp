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

    /**
     * Crea un nuevo usuario en el sistema después de validar que el correo electrónico y el número de teléfono no estén registrados previamente.
     * Si alguno de estos datos ya existe, se lanza una excepción indicando el conflicto.
     *
     * @param userDTO Objeto de transferencia con los datos del usuario a crear.
     * @return UserResponseDTO con la información del usuario creado.
     * @throws IllegalStateException si el correo o teléfono ya están en uso.
     */
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

    /**
     * Recupera la información detallada de un usuario por su identificador único (UUID).
     * Incluye los pagos realizados y el historial de eventos asociados al usuario.
     *
     * @param id Identificador único del usuario.
     * @return UserResponseDTO con los datos del usuario, pagos e historial de eventos.
     * @throws NotFoundException si no se encuentra un usuario con el ID proporcionado.
     */
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

    /**
     * Obtiene una lista paginada de todos los usuarios registrados en el sistema.
     *
     * @param pageable Parámetro de paginación y ordenamiento.
     * @return Page<UserResponseDTO> página de usuarios encontrados.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }

    /**
     * Actualiza los datos de un usuario existente, validando que el nuevo correo electrónico y teléfono no estén en uso por otro usuario.
     *
     * @param id Identificador único del usuario a actualizar.
     * @param userDTO Objeto de transferencia con los nuevos datos del usuario.
     * @return UserResponseDTO con la información actualizada del usuario.
     * @throws NotFoundException si el usuario no existe.
     * @throws IllegalStateException si el nuevo correo o teléfono ya están en uso por otro usuario.
     */
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

    /**
     * Elimina un usuario del sistema por su identificador único (UUID).
     *
     * @param id Identificador único del usuario a eliminar.
     * @throws NotFoundException si el usuario no existe.
     */
    @Override
    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Verifica si existe un usuario registrado con el correo electrónico proporcionado.
     *
     * @param email Correo electrónico a verificar.
     * @return true si existe un usuario con ese correo, false en caso contrario.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByCorreo(email);
    }

    /**
     * Verifica si existe un usuario registrado con el número de teléfono proporcionado.
     *
     * @param phone Número de teléfono a verificar.
     * @return true si existe un usuario con ese teléfono, false en caso contrario.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByPhone(String phone) {
        return userRepository.existsByTelefono(phone);
    }
}
