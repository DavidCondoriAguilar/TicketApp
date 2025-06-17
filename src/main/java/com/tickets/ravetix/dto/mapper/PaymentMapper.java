package com.tickets.ravetix.dto.mapper;

import com.tickets.ravetix.dto.payment.PaymentCreateDTO;
import com.tickets.ravetix.dto.payment.PaymentResponseDTO;
import com.tickets.ravetix.dto.ticket.TicketSimpleDTO;
import com.tickets.ravetix.dto.user.UserSimpleDTO;
import com.tickets.ravetix.entity.Payment;
import com.tickets.ravetix.entity.Ticket;
import com.tickets.ravetix.entity.User;
import com.tickets.ravetix.enums.EstadoPago;
import com.tickets.ravetix.enums.MetodoPago;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Mapper(
        componentModel = "spring",
        uses = {TicketMapper.class, UserMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class PaymentMapper implements BaseMapper<Payment, PaymentCreateDTO, Object, PaymentResponseDTO> {

    @Autowired
    protected TicketMapper ticketMapper;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "fechaActualizacion", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "ticket", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "estado", expression = "java(com.tickets.ravetix.enums.EstadoPago.PENDIENTE)")
    @Mapping(target = "monto", expression = "java(calculateAmount(createDto))")
    @Mapping(target = "metodoPago", expression = "java(convertMetodoPagoIdToEnum(createDto.getMetodoPagoId()))")
    @Mapping(target = "fechaPago", ignore = true)
    @Override
    public abstract Payment toEntity(PaymentCreateDTO createDto);

    @Override
    public PaymentResponseDTO toDto(Payment entity) {
        if (entity == null) {
            return null;
        }

        // Create a new builder and set all fields directly
        PaymentResponseDTO dto = new PaymentResponseDTO();
        
        // Set BaseDTO fields
        if (entity.getId() != null) {
            dto.setId(entity.getId().toString());
        }
        if (entity.getFechaCreacion() != null) {
            dto.setFechaCreacion(entity.getFechaCreacion());
        }
        if (entity.getFechaActualizacion() != null) {
            dto.setFechaActualizacion(entity.getFechaActualizacion());
        }
        if (entity.getVersion() != null) {
            dto.setVersion(entity.getVersion());
        }
        
        // Set Payment specific fields
        dto.setMonto(entity.getMonto());
        dto.setMetodoPago(entity.getMetodoPago());
        dto.setEstado(entity.getEstado());
        dto.setFechaPago(entity.getFechaPago());
        dto.setTicket(mapTicketToSimple(entity.getTicket()));
        dto.setUsuario(mapUserToSimple(entity.getUsuario()));
        
        return dto;
    }

    @Override
    public Payment updateEntity(Object updateDto, @MappingTarget Payment entity) {
        throw new UnsupportedOperationException("La actualización directa de pagos no está soportada");
    }

    @Named("mapUuidToString")
    protected String mapUuidToString(UUID id) {
        return id != null ? id.toString() : null;
    }

    protected BigDecimal calculateAmount(PaymentCreateDTO createDto) {
        return createDto.getComision() != null ? createDto.getComision() : BigDecimal.ZERO;
    }

    @Named("mapTicketToSimple")
    protected TicketSimpleDTO mapTicketToSimple(Ticket ticket) {
        if (ticket == null) {
            return null;
        }

        TicketSimpleDTO dto = new TicketSimpleDTO();
        dto.setId(ticket.getId() != null ? ticket.getId().toString() : null);
        return dto;
    }

    protected MetodoPago convertMetodoPagoIdToEnum(Long metodoPagoId) {
        if (metodoPagoId == null) {
            return null;
        }
        MetodoPago[] values = MetodoPago.values();
        int index = metodoPagoId.intValue() - 1;
        return (index >= 0 && index < values.length) ? values[index] : null;
    }

    @Named("mapUserToSimple")
    protected UserSimpleDTO mapUserToSimple(User user) {
        if (user == null) {
            return null;
        }

        UserSimpleDTO dto = new UserSimpleDTO();
        dto.setId(user.getId() != null ? user.getId().toString() : null);
        dto.setNombre(user.getNombre());
        dto.setCorreo(user.getCorreo());
        return dto;
    }

    public java.util.List<PaymentResponseDTO> toDtoList(java.util.List<Payment> payments) {
        if (payments == null) {
            return null;
        }
        return payments.stream()
                .map(this::toDto)
                .collect(java.util.stream.Collectors.toList());
    }
}