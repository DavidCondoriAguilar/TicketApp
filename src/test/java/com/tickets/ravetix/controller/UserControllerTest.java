package com.tickets.ravetix.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tickets.ravetix.dto.user.UserCreateDTO;
import com.tickets.ravetix.dto.user.UserUpdateDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateUser() throws Exception {
        UserCreateDTO userDTO = new UserCreateDTO();
        userDTO.setNombre("Juan");
        userDTO.setApellido("Pérez");
        userDTO.setCorreo("juan.perez+" + UUID.randomUUID() + "@example.com");
        userDTO.setTelefono("555" + UUID.randomUUID().toString().substring(0, 7));
        userDTO.setPassword("Password1!");
        userDTO.setConfirmPassword("Password1!");
        userDTO.setTerminosAceptados(true);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    void shouldGetUserById() throws Exception {
        UserCreateDTO userDTO = new UserCreateDTO();
        userDTO.setNombre("Ana");
        userDTO.setApellido("García");
        userDTO.setCorreo("ana.garcia+" + UUID.randomUUID() + "@example.com");
        userDTO.setTelefono("555" + UUID.randomUUID().toString().substring(0, 7));
        userDTO.setPassword("Password1!");
        userDTO.setConfirmPassword("Password1!");
        userDTO.setTerminosAceptados(true);

        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UUID userId = UUID.fromString(objectMapper.readTree(response).get("id").asText());

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.nombre").value("Ana"));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        UserCreateDTO userDTO = new UserCreateDTO();
        userDTO.setNombre("Carlos");
        userDTO.setApellido("Lopez");
        userDTO.setCorreo("carlos.lopez+" + UUID.randomUUID() + "@example.com");
        userDTO.setTelefono("555" + UUID.randomUUID().toString().substring(0, 7));
        userDTO.setPassword("Password1!");
        userDTO.setConfirmPassword("Password1!");
        userDTO.setTerminosAceptados(true);

        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andReturn().getResponse().getContentAsString();

        UUID userId = UUID.fromString(objectMapper.readTree(response).get("id").asText());

        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setNombre("Carlos Modificado");
        updateDTO.setApellido("Lopez");
        updateDTO.setCorreo("carlos.lopez+" + UUID.randomUUID() + "@example.com");
        updateDTO.setTelefono("555555555");
        updateDTO.setNewPassword("Password1!");
        updateDTO.setConfirmNewPassword("Password1!");
        updateDTO.setCurrentPassword("Password1!"); // si es requerido para cambiar la contraseña


        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Carlos Modificado"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        UserCreateDTO userDTO = new UserCreateDTO();
        userDTO.setNombre("Borrar");
        userDTO.setApellido("Usuario");
        userDTO.setCorreo("borrar.usuario+" + UUID.randomUUID() + "@example.com");
        userDTO.setTelefono("000000000");
        userDTO.setPassword("Password1!");
        userDTO.setConfirmPassword("Password1!");
        userDTO.setTerminosAceptados(true);

        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andReturn().getResponse().getContentAsString();

        UUID userId = UUID.fromString(objectMapper.readTree(response).get("id").asText());

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());
    }
}