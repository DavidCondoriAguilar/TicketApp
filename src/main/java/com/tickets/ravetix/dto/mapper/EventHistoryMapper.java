package com.tickets.ravetix.dto.mapper;

import com.tickets.ravetix.dto.event.EventSimpleDTO;
import com.tickets.ravetix.dto.eventhistory.EventHistoryResponseDTO;
import com.tickets.ravetix.dto.user.UserSimpleDTO;
import com.tickets.ravetix.entity.Event;
import com.tickets.ravetix.entity.EventHistory;
import com.tickets.ravetix.entity.Location;
import com.tickets.ravetix.entity.User;
import org.mapstruct.*;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * Mapper for converting between EventHistory entity and DTOs.
 */
@Mapper(
    componentModel = "spring",
    uses = {EventMapper.class, UserMapper.class}
)
public abstract class EventHistoryMapper {

    public EventHistoryResponseDTO toDto(EventHistory entity) {
        if (entity == null) {
            return null;
        }

        return EventHistoryResponseDTO.builder()
                .id(entity.getId() != null ? entity.getId().toString() : null)
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .evento(mapEventToSimpleDTO(entity.getEvento()))
                .usuario(mapUserToSimpleDTO(entity.getUsuario()))
                .asistenciaConfirmada(entity.isAsistenciaConfirmada())
                .calificacion(entity.getCalificacion())
                .comentario(entity.getComentario())
                .fechaRegistro(entity.getFechaCreacion())
                .build();
    }

    @Named("mapUuidToString")
    protected String mapUuidToString(UUID id) {
        return id != null ? id.toString() : null;
    }

    @Named("mapEventToSimpleDTO")
    protected EventSimpleDTO mapEventToSimpleDTO(Event event) {
        if (event == null) {
            return null;
        }
        
        EventSimpleDTO dto = new EventSimpleDTO();
        dto.setId(event.getId() != null ? event.getId().toString() : null);
        dto.setNombre(event.getNombre());
        dto.setFechaHoraInicio(event.getFechaHoraInicio());
        dto.setUbicacion(mapLocationToString(event.getUbicacion()));
        
        return dto;
    }
    
    @Named("mapUserToSimpleDTO")
    protected UserSimpleDTO mapUserToSimpleDTO(User user) {
        if (user == null) {
            return null;
        }
        
        UserSimpleDTO dto = new UserSimpleDTO();
        dto.setId(user.getId() != null ? user.getId().toString() : null);
        dto.setNombre(user.getNombre());
        dto.setCorreo(user.getCorreo());
        
        return dto;
    }
    
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
