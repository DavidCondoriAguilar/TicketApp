package com.tickets.ravetix.dto.mapper;

import com.tickets.ravetix.dto.payment.PaymentResponseDTO;
import com.tickets.ravetix.dto.ticket.TicketResponseDTO;
import com.tickets.ravetix.dto.user.*;
import com.tickets.ravetix.entity.*;
import com.tickets.ravetix.enums.TicketState;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Mapper for converting between User entity and DTOs.
 */
@Mapper(
    componentModel = "spring",
    uses = {TicketMapper.class, PaymentMapper.class, EventHistoryMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper implements BaseMapper<User, UserCreateDTO, UserUpdateDTO, UserResponseDTO> {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tickets", ignore = true) // Se manejará manualmente si es necesario
    @Mapping(target = "pagos", ignore = true) // Se manejará manualmente si es necesario
    @Mapping(target = "historialEventos", ignore = true) // Se manejará manualmente si es necesario
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Override
    public abstract User toEntity(UserCreateDTO createDto);

    @Override
    public UserResponseDTO toDto(User entity) {
        if (entity == null) {
            return null;
        }

        return UserResponseDTO.builder()
                .id(entity.getId() != null ? entity.getId().toString() : null)
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .version(entity.getVersion())
                .nombre(entity.getNombre())
                .correo(entity.getCorreo())
                .telefono(entity.getTelefono())
                .tickets(mapTickets(entity))
                .pagos(mapPagos(entity.getPagos()))
                .historialEventos(mapHistorialEventos(entity.getHistorialEventos()))
                .build();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tickets", ignore = true)
    @Mapping(target = "pagos", ignore = true)
    @Mapping(target = "historialEventos", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", expression = "java(java.time.LocalDateTime.now())")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Override
    public abstract User updateEntity(UserUpdateDTO updateDto, @MappingTarget User entity);

    // Métodos auxiliares para mapear relaciones
    protected java.util.List<TicketResponseDTO> mapTickets(User user) {
        if (user.getTickets() == null) {
            return new ArrayList<>();
        }
        return user.getTickets().stream()
                .map(ticket -> {
                    var dto = new TicketResponseDTO();
                    dto.setId(ticket.getId().toString());
                    dto.setPrecio(ticket.getPrecio());
                    dto.setFechaCompra(ticket.getFechaCompra());
                    // Incluir el estado completo del ticket
                    dto.setEstado(ticket.getEstado().name());
                    dto.setPagado(ticket.getEstado() == TicketState.PAGADO);
                    
                    // Mapear evento con más detalles
                    if (ticket.getEvento() != null) {
                        var eventoDto = new com.tickets.ravetix.dto.event.EventSimpleDTO();
                        eventoDto.setId(ticket.getEvento().getId().toString());
                        eventoDto.setNombre(ticket.getEvento().getNombre());
                        // Incluir fechas del evento
                        eventoDto.setFechaHoraInicio(ticket.getEvento().getFechaHoraInicio());
                        eventoDto.setFechaHoraFin(ticket.getEvento().getFechaHoraFin());
                        eventoDto.setEstado(ticket.getEvento().getEstado());
                        dto.setEvento(eventoDto);
                    }
                    
                    // Mapear zona
                    if (ticket.getZona() != null) {
                        var zonaDto = new com.tickets.ravetix.dto.zone.ZoneSimpleDTO();
                        zonaDto.setId(ticket.getZona().getId().toString());
                        zonaDto.setNombre(ticket.getZona().getNombre());
                        // Solo incluir campos básicos de la zona
                        dto.setZona(zonaDto);
                    }
                    
                    return dto;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    @Autowired
    protected PaymentMapper paymentMapper;
    
    @Autowired
    protected EventHistoryMapper eventHistoryMapper;
    
    @Named("mapPagos")
    public List<PaymentResponseDTO> mapPagos(Collection<Payment> pagos) {
        if (pagos == null || pagos.isEmpty()) {
            return new ArrayList<>();
        }
        return pagos.stream()
                .map(paymentMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
    }

    protected java.util.List<com.tickets.ravetix.dto.eventhistory.EventHistoryResponseDTO> mapHistorialEventos(java.util.Collection<EventHistory> historiales) {
        if (historiales == null || historiales.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        return historiales.stream()
                .map(eventHistoryMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
    }
}
