package com.tickets.ravetix.service;

import com.tickets.ravetix.dto.eventhistory.EventHistoryResponseDTO;
import com.tickets.ravetix.dto.mapper.UserMapper;
import com.tickets.ravetix.dto.payment.PaymentResponseDTO;
import com.tickets.ravetix.dto.user.UserCreateDTO;
import com.tickets.ravetix.dto.user.UserResponseDTO;
import com.tickets.ravetix.dto.user.UserUpdateDTO;
import com.tickets.ravetix.entity.User;
import com.tickets.ravetix.exception.NotFoundException;
import com.tickets.ravetix.repository.UserRepository;
import com.tickets.ravetix.service.impl.UserServiceImpl;
import com.tickets.ravetix.service.interfac.EventHistoryService;
import com.tickets.ravetix.service.interfac.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PaymentService paymentService;
    @Mock
    private EventHistoryService eventHistoryService;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUserShouldCreateUserWhenEmailAndPhoneAreUnique() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setCorreo("test@mail.com");
        dto.setTelefono("123456789");
        User user = new User();
        User savedUser = new User();
        UserResponseDTO responseDTO = new UserResponseDTO();

        when(userRepository.existsByCorreo(dto.getCorreo())).thenReturn(false);
        when(userRepository.existsByTelefono(dto.getTelefono())).thenReturn(false);
        when(userMapper.toEntity(dto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(responseDTO);

        UserResponseDTO result = userService.createUser(dto);

        assertNotNull(result);
        verify(userRepository).existsByCorreo(dto.getCorreo());
        verify(userRepository).existsByTelefono(dto.getTelefono());
        verify(userRepository).save(user);
        verify(userMapper).toDto(savedUser);
    }

    @Test
    void createUserShouldThrowWhenEmailExists() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setCorreo("test@mail.com");
        when(userRepository.existsByCorreo(dto.getCorreo())).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> userService.createUser(dto));
        verify(userRepository).existsByCorreo(dto.getCorreo());
    }

    @Test
    void createUserShouldThrowWhenPhoneExists() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setCorreo("test@mail.com");
        dto.setTelefono("123456789");
        when(userRepository.existsByCorreo(dto.getCorreo())).thenReturn(false);
        when(userRepository.existsByTelefono(dto.getTelefono())).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> userService.createUser(dto));
        verify(userRepository).existsByTelefono(dto.getTelefono());
    }

    @Test
    void getUserByIdShouldReturnUserWithPaymentsAndHistory() {
        UUID id = UUID.randomUUID();
        User user = new User();
        UserResponseDTO userDTO = new UserResponseDTO();
        List<PaymentResponseDTO> payments = List.of(new PaymentResponseDTO());
        List<EventHistoryResponseDTO> history = List.of(new EventHistoryResponseDTO());

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDTO);
        when(paymentService.getPaymentsByUserId(id, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(payments));
        when(eventHistoryService.getEventHistoryByUserId(id, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(history));

        UserResponseDTO result = userService.getUserById(id);

        assertNotNull(result);
        assertEquals(payments, result.getPagos());
        assertEquals(history, result.getHistorialEventos());
        verify(userRepository).findById(id);
        verify(paymentService).getPaymentsByUserId(id, Pageable.unpaged());
        verify(eventHistoryService).getEventHistoryByUserId(id, Pageable.unpaged());
    }

    @Test
    void getUserByIdShouldThrowWhenUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(id));
        verify(userRepository).findById(id);
    }

    @Test
    void getAllUsersShouldReturnPagedUsers() {
        Pageable pageable = Pageable.unpaged();
        User user = new User();
        UserResponseDTO dto = new UserResponseDTO();
        Page<User> userPage = new PageImpl<>(List.of(user));

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toDto(user)).thenReturn(dto);

        Page<UserResponseDTO> result = userService.getAllUsers(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository).findAll(pageable);
        verify(userMapper).toDto(user);
    }

    @Test
    void updateUserShouldUpdateWhenEmailAndPhoneAreUnique() {
        UUID id = UUID.randomUUID();
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setCorreo("new@mail.com");
        dto.setTelefono("987654321");
        User user = new User();
        user.setCorreo("old@mail.com");
        user.setTelefono("123456789");
        User updatedUser = new User();
        UserResponseDTO responseDTO = new UserResponseDTO();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.existsByCorreo(dto.getEmail())).thenReturn(false);
        when(userRepository.existsByTelefono(dto.getTelefono())).thenReturn(false);
        doNothing().when(userMapper).updateEntity(dto, user);
        when(userRepository.save(user)).thenReturn(updatedUser);
        when(userMapper.toDto(updatedUser)).thenReturn(responseDTO);

        UserResponseDTO result = userService.updateUser(id, dto);

        assertNotNull(result);
        verify(userRepository).findById(id);
        verify(userRepository).existsByCorreo(dto.getEmail());
        verify(userRepository).existsByTelefono(dto.getTelefono());
        verify(userMapper).updateEntity(dto, user);
        verify(userRepository).save(user);
        verify(userMapper).toDto(updatedUser);
    }

    @Test
    void updateUserShouldThrowWhenUserNotFound() {
        UUID id = UUID.randomUUID();
        UserUpdateDTO dto = new UserUpdateDTO();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(id, dto));
        verify(userRepository).findById(id);
    }

    @Test
    void updateUserShouldThrowWhenEmailExists() {
        UUID id = UUID.randomUUID();
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setCorreo("new@mail.com");
        User user = new User();
        user.setCorreo("old@mail.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.existsByCorreo(dto.getEmail())).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> userService.updateUser(id, dto));
        verify(userRepository).existsByCorreo(dto.getEmail());
    }

    @Test
    void updateUserShouldThrowWhenPhoneExists() {
        UUID id = UUID.randomUUID();
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setTelefono("987654321");
        User user = new User();
        user.setTelefono("123456789");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.existsByTelefono(dto.getTelefono())).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> userService.updateUser(id, dto));
        verify(userRepository).existsByTelefono(dto.getTelefono());
    }

    @Test
    void deleteUserShouldDeleteWhenUserExists() {
        UUID id = UUID.randomUUID();
        when(userRepository.existsById(id)).thenReturn(true);
        doNothing().when(userRepository).deleteById(id);

        assertDoesNotThrow(() -> userService.deleteUser(id));
        verify(userRepository).existsById(id);
        verify(userRepository).deleteById(id);
    }

    @Test
    void deleteUserShouldThrowWhenUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.existsById(id)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.deleteUser(id));
        verify(userRepository).existsById(id);
    }

    @Test
    void existsByEmailShouldReturnCorrectValue() {
        when(userRepository.existsByCorreo("mail@mail.com")).thenReturn(true);
        assertTrue(userService.existsByEmail("mail@mail.com"));
        verify(userRepository).existsByCorreo("mail@mail.com");
    }

    @Test
    void existsByPhoneShouldReturnCorrectValue() {
        when(userRepository.existsByTelefono("123456789")).thenReturn(false);
        assertFalse(userService.existsByPhone("123456789"));
        verify(userRepository).existsByTelefono("123456789");
    }
}