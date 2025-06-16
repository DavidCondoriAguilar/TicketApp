package com.tickets.ravetix.dto.mapper;

import com.tickets.ravetix.dto.zone.ZoneCreateDTO;
import com.tickets.ravetix.dto.zone.ZoneRequestDTO;
import com.tickets.ravetix.dto.zone.ZoneResponseDTO;
import com.tickets.ravetix.dto.zone.ZoneUpdateDTO;
import com.tickets.ravetix.entity.Zone;
import com.tickets.ravetix.util.MappingUtil;
import com.tickets.ravetix.repository.EventRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Mapper for converting between Zone entity and DTOs.
 */
@Mapper(componentModel = "spring", uses = {TicketMapper.class})
public abstract class ZoneMapper implements BaseMapper<Zone, ZoneRequestDTO, ZoneRequestDTO, ZoneResponseDTO> {

    @Autowired
    protected EventRepository eventRepository;
    @Mapping(target = "precioBase", source = "precioBase")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tickets", ignore = true)
    @Mapping(target = "evento", ignore = true) // Event association should be handled in the service layer
    @Override
    public abstract Zone toEntity(ZoneRequestDTO createDto);
    
    /**
     * Maps a ZoneCreateDTO to a Zone entity.
     * Similar to toEntity(ZoneRequestDTO) but specifically for ZoneCreateDTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tickets", ignore = true)
    @Mapping(target = "evento", ignore = true)
    @Mapping(target = "precioBase", source = "precioBase")
    public abstract Zone toEntity(ZoneCreateDTO createDto);

    @Override
    @Mapping(target = "entradasDisponibles", expression = "java(calculateAvailableSpots(entity))")
    public ZoneResponseDTO toDto(Zone entity) {
        if (entity == null) {
            return null;
        }
        
        ZoneResponseDTO dto = new ZoneResponseDTO();
        // Map the ID explicitly
        if (entity.getId() != null) {
            dto.setId(entity.getId().toString());
        }
        
        // Map other properties
        dto.setNombre(entity.getNombre());
        dto.setCapacidad(entity.getCapacidad());
        dto.setPrecioBase(entity.getPrecioBase());
        dto.setTipo(entity.getTipo());
        
        // Set other fields as needed
        dto.setFechaCreacion(entity.getFechaCreacion());
        dto.setFechaActualizacion(entity.getFechaActualizacion());
        
        return dto;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "evento", ignore = true)
    @Mapping(target = "tickets", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Override
    public abstract void updateEntity(ZoneRequestDTO updateDto, @MappingTarget Zone entity);

    protected Integer calculateAvailableSpots(Zone zone) {
        if (zone.getCapacidad() == null) {
            return 0;
        }
        int ticketsSold = zone.getTickets() != null ? zone.getTickets().size() : 0;
        return Math.max(0, zone.getCapacidad() - ticketsSold);
    }
    
    /**
     * Maps Location object to a formatted string using MappingUtil
     */
    protected String mapLocationToString(com.tickets.ravetix.entity.Location location) {
        return MappingUtil.mapLocationToString(location);
    }
    
    /**
     * Updates a Zone entity from a ZoneUpdateDTO.
     * @param dto The DTO containing the updated values
     * @param zone The entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "evento", ignore = true)
    @Mapping(target = "tickets", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateZoneFromDto(ZoneUpdateDTO dto, @MappingTarget Zone zone);
}
