package com.tickets.ravetix.dto.mapper;

import com.tickets.ravetix.dto.eventhistory.EventHistoryResponseDTO;
import com.tickets.ravetix.entity.EventHistory;
import com.tickets.ravetix.entity.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.util.StringUtils;

/**
 * Mapper for converting between EventHistory entity and DTOs.
 */
@Mapper(componentModel = "spring")
public abstract class EventHistoryMapper {

    @Mapping(target = "evento.ubicacion", source = "evento.ubicacion", qualifiedByName = "mapLocationToString")
    @Mapping(target = "usuario", source = "usuario")
    public abstract EventHistoryResponseDTO toDto(EventHistory entity);
    
    @Named("mapLocationToString")
    protected String mapLocationToString(Location location) {
        if (location == null) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        
        if (StringUtils.hasText(location.getDireccion())) {
            sb.append(location.getDireccion());
        }
        
        if (StringUtils.hasText(location.getCiudad())) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(location.getCiudad());
        }
        
        if (StringUtils.hasText(location.getPais())) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(location.getPais());
        }
        
        if (StringUtils.hasText(location.getCodigoPostal())) {
            if (sb.length() > 0) sb.append(" ");
            sb.append("(").append(location.getCodigoPostal()).append(")");
        }
        
        return sb.length() > 0 ? sb.toString() : null;
    }
}
