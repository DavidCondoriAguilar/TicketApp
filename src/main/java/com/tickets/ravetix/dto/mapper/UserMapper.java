package com.tickets.ravetix.dto.mapper;

import com.tickets.ravetix.dto.ticket.TicketResponseDTO;
import com.tickets.ravetix.dto.user.*;
import com.tickets.ravetix.entity.User;
import com.tickets.ravetix.enums.TicketState;
import org.mapstruct.*;

import java.util.ArrayList;

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

    @Mapping(target = "id", source = "id")
    @Mapping(target = "fechaCreacion", source = "fechaCreacion")
    @Mapping(target = "fechaActualizacion", source = "fechaActualizacion")
    @Mapping(target = "version", source = "version")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "correo", source = "correo")
    @Mapping(target = "telefono", source = "telefono")
    @Mapping(target = "tickets", expression = "java(mapTickets(entity))")
    @Mapping(target = "pagos", expression = "java(mapPagos(entity))")
    @Mapping(target = "historialEventos", expression = "java(mapHistorialEventos(entity))")
    @Override
    public abstract UserResponseDTO toDto(User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tickets", ignore = true)
    @Mapping(target = "pagos", ignore = true)
    @Mapping(target = "historialEventos", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", expression = "java(java.time.LocalDateTime.now())")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Override
    public abstract void updateEntity(UserUpdateDTO updateDto, @MappingTarget User entity);

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

    protected java.util.List<com.tickets.ravetix.dto.payment.PaymentResponseDTO> mapPagos(User user) {
        if (user.getPagos() == null) {
            return new java.util.ArrayList<>();
        }
        return user.getPagos().stream()
                .map(pago -> {
                    var dto = new com.tickets.ravetix.dto.payment.PaymentResponseDTO();
                    dto.setId(pago.getId().toString());
                    dto.setMonto(pago.getMonto());
                    // Usar el valor del enum directamente
                    dto.setMetodoPago(pago.getMetodoPago());
                    dto.setFechaPago(pago.getFechaPago());
                    dto.setEstado(pago.getEstado());
                    
                    // Mapear ticket si existe - asumiendo que PaymentResponseDTO tiene un campo 'ticket' de tipo TicketSimpleDTO
                    if (pago.getTicket() != null) {
                        var ticketDto = new com.tickets.ravetix.dto.ticket.TicketSimpleDTO();
                        ticketDto.setId(pago.getTicket().getId().toString());
                        // Agregar más campos del ticket si es necesario
                        dto.setTicket(ticketDto);
                    }
                    
                    return dto;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    protected java.util.List<com.tickets.ravetix.dto.eventhistory.EventHistoryResponseDTO> mapHistorialEventos(User user) {
        if (user.getHistorialEventos() == null) {
            return new java.util.ArrayList<>();
        }
        return user.getHistorialEventos().stream()
                .map(historial -> {
                    var dto = new com.tickets.ravetix.dto.eventhistory.EventHistoryResponseDTO();
                    dto.setId(historial.getId().toString());
                    dto.setAsistenciaConfirmada(historial.isAsistenciaConfirmada());
                    dto.setCalificacion(historial.getCalificacion());
                    dto.setComentario(historial.getComentario());
                    dto.setFechaRegistro(historial.getFechaCreacion());
                    
                    // Mapear evento relacionado si existe
                    if (historial.getEvento() != null) {
                        var eventoDto = new com.tickets.ravetix.dto.event.EventSimpleDTO();
                        eventoDto.setId(historial.getEvento().getId().toString());
                        eventoDto.setNombre(historial.getEvento().getNombre());
                        dto.setEvento(eventoDto);
                    }
                    
                    // Mapear usuario si es necesario
                    if (historial.getUsuario() != null) {
                        var usuarioDto = new com.tickets.ravetix.dto.user.UserSimpleDTO();
                        usuarioDto.setId(historial.getUsuario().getId().toString());
                        usuarioDto.setNombre(historial.getUsuario().getNombre());
                        dto.setUsuario(usuarioDto);
                    }
                    
                    return dto;
                })
                .collect(java.util.stream.Collectors.toList());
    }


}
