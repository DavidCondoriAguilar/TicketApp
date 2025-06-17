package com.tickets.ravetix.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tickets.ravetix.dto.zone.ZoneCreateDTO;
import com.tickets.ravetix.dto.zone.ZoneUpdateDTO;
import com.tickets.ravetix.entity.Event;
import com.tickets.ravetix.entity.Location;
import com.tickets.ravetix.enums.TipoZona;
import com.tickets.ravetix.repository.EventRepository;
import com.tickets.ravetix.repository.ZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ZoneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    private UUID eventId;

    @BeforeEach
    void setUp() {
        zoneRepository.deleteAll();
        eventRepository.deleteAll();
        Location location = new Location();
        location.setDireccion("Calle Falsa 123");
        location.setCiudad("Lima");
        location.setPais("Perú");
        // Crea un evento de prueba
        Event event = new Event();
        event.setNombre("Evento Test");
        event.setDescripcion("Descripción de prueba");
        event.setFechaHoraInicio(LocalDateTime.now().plusDays(1));
        event.setFechaHoraFin(LocalDateTime.now().plusDays(2));
        event.setUbicacion(location);
        event = eventRepository.save(event);
        eventId = event.getId();
    }

    @Test
    void shouldCreateZone() throws Exception {
        ZoneCreateDTO dto = new ZoneCreateDTO();
        dto.setNombre("VIP");
        dto.setCapacidad(100);
        dto.setPrecioBase(BigDecimal.valueOf(50.0));
        dto.setTipo(TipoZona.VIP);
        dto.setEventoId(eventId);

        mockMvc.perform(post("/api/events/{eventId}/zones", eventId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("VIP"));
    }

    @Test
    void shouldGetAllZonesByEventId() throws Exception {
        // Primero crea una zona
        ZoneCreateDTO dto = new ZoneCreateDTO();
        dto.setNombre("General");
        dto.setCapacidad(200);
        dto.setTipo(TipoZona.VIP);
        dto.setPrecioBase(BigDecimal.valueOf(20.0));
        dto.setEventoId(eventId);

        mockMvc.perform(post("/api/events/{eventId}/zones", eventId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/events/{eventId}/zones", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

//    @Test
//    void shouldUpdateZone() throws Exception {
//        // Crea una zona
//        ZoneCreateDTO createDTO = new ZoneCreateDTO();
//        createDTO.setNombre("Platea");
//        createDTO.setCapacidad(150);
//        createDTO.setPrecioBase(BigDecimal.valueOf(30.0));
//        createDTO.setEventoId(eventId);
//
//        String response = mockMvc.perform(post("/api/events/{eventId}/zones", eventId)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(createDTO)))
//                .andReturn().getResponse().getContentAsString();
//
//        UUID zoneId = UUID.fromString(objectMapper.readTree(response).get("id").asText());
//
//        ZoneUpdateDTO updateDTO = new ZoneUpdateDTO();
//        updateDTO.setNombre("Platea Actualizada");
//        updateDTO.setCapacidad(180);
//        updateDTO.setPrecio(BigDecimal.valueOf(35.0));
//        updateDTO.setEventoId(eventId);
//
//        mockMvc.perform(put("/api/events/{eventId}/zones/{zoneId}", eventId, zoneId)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(updateDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.nombre").value("Platea Actualizada"));
//    }

    @Test
    void shouldDeleteZone() throws Exception {
        // Crea una zona
        ZoneCreateDTO createDTO = new ZoneCreateDTO();
        createDTO.setNombre("Eliminar");
        createDTO.setCapacidad(50);
        createDTO.setPrecioBase(BigDecimal.valueOf(10.0));
        createDTO.setTipo(TipoZona.VIP);
        createDTO.setEventoId(eventId);

        String response = mockMvc.perform(post("/api/events/{eventId}/zones", eventId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andReturn().getResponse().getContentAsString();

        UUID zoneId = UUID.fromString(objectMapper.readTree(response).get("id").asText());

        mockMvc.perform(delete("/api/events/{eventId}/zones/{zoneId}", eventId, zoneId))
                .andExpect(status().isNoContent());
    }
}