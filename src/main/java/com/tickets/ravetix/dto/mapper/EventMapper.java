package com.tickets.ravetix.dto.mapper;

import com.tickets.ravetix.dto.event.EventCreateDTO;
import com.tickets.ravetix.dto.event.EventResponseDTO;
import com.tickets.ravetix.dto.event.EventUpdateDTO;
import com.tickets.ravetix.entity.Event;
import com.tickets.ravetix.entity.Location;
import com.tickets.ravetix.repository.UserRepository;
import com.tickets.ravetix.util.MappingUtil;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * Mapper for converting between Event entity and DTOs.
 * MapStruct will automatically implement this interface at compile time.
 */
@Mapper(
    componentModel = "spring",
    uses = {ZoneMapper.class, UserMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class EventMapper implements BaseMapper<Event, EventCreateDTO, EventUpdateDTO, EventResponseDTO> {

    @Autowired
    protected UserRepository userRepository;

    /**
     * Converts EventCreateDTO to Event entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "zonas", ignore = true)
    @Mapping(target = "tickets", ignore = true)
    @Mapping(target = "historial", ignore = true)
    @Mapping(target = "estado", constant = "CREADO")
    @Mapping(target = "fechaCreacion", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "fechaActualizacion", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "ubicacion", source = "ubicacion")
    @Mapping(target = "fechaHoraInicio", source = "fechaHoraInicio")
    @Mapping(target = "fechaHoraFin", source = "fechaHoraFin")
    @Mapping(target = "duracionHoras", ignore = true) // Will be calculated in the entity
    @Override
    public abstract Event toEntity(EventCreateDTO createDto);

    /**
     * Converts Event entity to EventResponseDTO
     */
    @Mapping(target = "capacidadTotal", expression = "java(calculateTotalCapacity(entity))")
    @Mapping(target = "entradasVendidas", expression = "java(calculateTicketsSold(entity))")
    @Mapping(target = "entradasDisponibles", expression = "java(calculateAvailableTickets(entity))")
    @Mapping(target = "ubicacion", source = "ubicacion", qualifiedByName = "mapLocationToResponse")
    @Mapping(target = "fechaHoraInicio", source = "fechaHoraInicio")
    @Mapping(target = "fechaHoraFin", source = "fechaHoraFin")
    @Mapping(target = "id", expression = "java(mapUuidToString(entity.getId()))")
    @Override
    public abstract EventResponseDTO toDto(Event entity);

    /**
     * Updates Event entity from EventUpdateDTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "zonas", ignore = true)
    @Mapping(target = "tickets", ignore = true)
    @Mapping(target = "historial", ignore = true)
    @Mapping(target = "fechaActualizacion", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "ubicacion", source = "ubicacion")
    @Mapping(target = "fechaHoraInicio", source = "fechaHoraInicio")
    @Mapping(target = "fechaHoraFin", source = "fechaHoraFin")
    @Mapping(target = "duracionHoras", ignore = true) // Will be calculated in the entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Override
    public abstract void updateEntity(EventUpdateDTO updateDto, @MappingTarget Event entity);

    /**
     * Calculates total capacity from all zones
     */
    protected Integer calculateTotalCapacity(Event event) {
        if (event.getZonas() == null || event.getZonas().isEmpty()) {
            return 0;
        }
        return event.getZonas().stream()
                .mapToInt(zone -> zone.getCapacidad() != null ? zone.getCapacidad() : 0)
                .sum();
    }

    /**
     * Counts total tickets sold for the event
     */
    protected Integer calculateTicketsSold(Event event) {
        return (event.getTickets() != null) ? event.getTickets().size() : 0;
    }

    /**
     * Calculates available tickets
     */
    protected Integer calculateAvailableTickets(Event event) {
        return calculateTotalCapacity(event) - calculateTicketsSold(event);
    }
    
    /**
     * Helper method to safely convert UUID to String
     * @param uuid the UUID to convert
     * @return String representation of the UUID, or null if the input is null
     */
    protected String mapUuidToString(UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }
    
    /**
     * Maps Location object to a formatted string for response DTO using MappingUtil
     */
    @Named("mapLocationToResponse")
    protected String mapLocationToResponse(Location location) {
        return MappingUtil.mapLocationToString(location);
    }
}
