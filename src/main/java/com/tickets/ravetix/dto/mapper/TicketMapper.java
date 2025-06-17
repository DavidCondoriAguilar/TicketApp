package com.tickets.ravetix.dto.mapper;

import com.tickets.ravetix.dto.event.EventSimpleDTO;
import com.tickets.ravetix.dto.ticket.TicketCreateDTO;
import com.tickets.ravetix.dto.ticket.TicketResponseDTO;
import com.tickets.ravetix.entity.*;
import com.tickets.ravetix.util.MappingUtil;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Ticket entity and DTOs.
 */
@Mapper(
    componentModel = "spring",
    uses = {EventMapper.class, ZoneMapper.class, UserMapper.class}
)
public abstract class TicketMapper implements BaseMapper<Ticket, TicketCreateDTO, Object, TicketResponseDTO> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "evento", ignore = true)
    @Mapping(target = "zona", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "pago", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    public abstract Ticket toEntity(TicketCreateDTO createDto);

    @Override
    @Mapping(target = "evento", source = "evento", qualifiedByName = "mapEventoToSimpleDTO")
    @Mapping(target = "zona", source = "zona", qualifiedByName = "mapZonaToSimpleDTO")
    @Mapping(target = "usuario", source = "usuario", qualifiedByName = "mapUsuarioToSimpleDTO")
    @Mapping(target = "fechaCompra", source = "fechaCompra")
    @Mapping(target = "precio", source = "precio")
    @Mapping(target = "pagado", expression = "java(entity.getPago() != null)")
    public TicketResponseDTO toDto(Ticket entity) {
        if (entity == null) {
            return null;
        }
        
        return TicketResponseDTO.builder()
                .id(entity.getId() != null ? entity.getId().toString() : null)
                .evento(mapEventoToSimpleDTO(entity.getEvento()))
                .zona(mapZonaToSimpleDTO(entity.getZona()))
                .usuario(mapUsuarioToSimpleDTO(entity.getUsuario()))
                .precio(entity.getPrecio())
                .fechaCompra(entity.getFechaCompra())
                .pagado(entity.getPago() != null)
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }
    
    // ID mapping is now handled directly in the @Mapping annotation
    
    @Named("mapUsuarioToSimpleDTO")
    protected com.tickets.ravetix.dto.user.UserSimpleDTO mapUsuarioToSimpleDTO(User user) {
        if (user == null) {
            return null;
        }
        
        com.tickets.ravetix.dto.user.UserSimpleDTO dto = new com.tickets.ravetix.dto.user.UserSimpleDTO();
        dto.setId(user.getId() != null ? user.getId().toString() : null);
        dto.setNombre(user.getNombre());
        dto.setCorreo(user.getCorreo());

        return dto;
    }
    
    @Named("mapZonaToSimpleDTO")
    protected com.tickets.ravetix.dto.zone.ZoneSimpleDTO mapZonaToSimpleDTO(Zone zone) {
        if (zone == null) {
            return null;
        }
        
        com.tickets.ravetix.dto.zone.ZoneSimpleDTO dto = new com.tickets.ravetix.dto.zone.ZoneSimpleDTO();
        dto.setId(zone.getId() != null ? zone.getId().toString() : null);
        dto.setNombre(zone.getNombre());
        dto.setPrecioBase(zone.getPrecioBase());
        dto.setTipo(zone.getTipo());
        // Solo incluir los campos definidos en ZoneSimpleDTO
        // Capacidad y entradasDisponibles no están en el DTO
        
        return dto;
    }
    
    @Named("mapEventoToSimpleDTO")
    protected EventSimpleDTO mapEventoToSimpleDTO(Event event) {
        if (event == null) {
            return null;
        }
        
        EventSimpleDTO dto = new EventSimpleDTO();
        dto.setId(event.getId() != null ? event.getId().toString() : null);
        dto.setNombre(event.getNombre());
        dto.setFechaHoraInicio(event.getFechaHoraInicio());
        
        // Verificar si el evento tiene ubicación antes de mapearla
        if (event.getUbicacion() != null) {
            dto.setUbicacion(mapLocationToString(event.getUbicacion()));
        }
        
        return dto;
    }
    
    @Named("mapLocationToString")
    protected String mapLocationToString(Location location) {
        return MappingUtil.mapLocationToString(location);
    }

    @Override
    public Ticket updateEntity(Object updateDto, @MappingTarget Ticket entity) {
        // No se implementa actualización directa para tickets
        throw new UnsupportedOperationException("La actualización directa de tickets no está soportada");
    }
    
    /**
     * Mapea una lista de entidades Ticket a una lista de DTOs
     */
    public List<TicketResponseDTO> toDtoList(List<Ticket> tickets) {
        if (tickets == null) {
            return null;
        }
        return tickets.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
