package com.tickets.ravetix.dto.mapper;

import com.tickets.ravetix.dto.payment.PaymentCreateDTO;
import com.tickets.ravetix.dto.payment.PaymentResponseDTO;
import com.tickets.ravetix.dto.ticket.TicketSimpleDTO;
import com.tickets.ravetix.dto.user.UserSimpleDTO;
import com.tickets.ravetix.entity.Payment;
import org.mapstruct.*;

import java.math.BigDecimal;

/**
 * Mapper for converting between Payment entity and DTOs.
 */
@Mapper(
    componentModel = "spring",
    uses = {TicketMapper.class, UserMapper.class}
)
public abstract class PaymentMapper implements BaseMapper<Payment, PaymentCreateDTO, Object, PaymentResponseDTO> {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "ticket", ignore = true) // Se manejará en el servicio
    @Mapping(target = "usuario", ignore = true) // Se manejará en el servicio
    @Mapping(target = "estado", constant = "PENDIENTE")
    @Mapping(target = "monto", expression = "java(calculateAmount(createDto))")
    @Mapping(target = "metodoPago", ignore = true) // Se manejará en el servicio
    @Mapping(target = "fechaPago", ignore = true)
    @Override
    public abstract Payment toEntity(PaymentCreateDTO createDto);

    @Mapping(target = "monto", source = "monto")
    @Mapping(target = "metodoPago", source = "metodoPago")
    @Mapping(target = "estado", source = "estado")
    @Mapping(target = "fechaPago", source = "fechaPago")
    @Mapping(target = "ticket", expression = "java(mapTicketToSimple(entity))")
    @Mapping(target = "usuario", expression = "java(mapUserToSimple(entity))")
    @Override
    public abstract PaymentResponseDTO toDto(Payment entity);

    @Override
    public void updateEntity(Object updateDto, @MappingTarget Payment entity) {
        // No se implementa actualización directa para pagos
        throw new UnsupportedOperationException("La actualización directa de pagos no está soportada");
    }
    
    protected BigDecimal calculateAmount(PaymentCreateDTO createDto) {
        // En una implementación real, esto podría calcularse basado en el ticket
        return BigDecimal.ZERO;
    }
    
    protected TicketSimpleDTO mapTicketToSimple(Payment payment) {
        if (payment.getTicket() == null) {
            return null;
        }
        // Implementar mapeo a TicketSimpleDTO si es necesario
        return new TicketSimpleDTO();
    }
    
    protected UserSimpleDTO mapUserToSimple(Payment payment) {
        if (payment.getUsuario() == null) {
            return null;
        }
        // Implementar mapeo a UserSimpleDTO si es necesario
        return new UserSimpleDTO();
    }
}
