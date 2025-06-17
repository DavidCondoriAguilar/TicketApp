package com.tickets.ravetix.enums;

/**
 * Enumeración que representa los posibles estados de un pago.
 */
public enum EstadoPago {
    /** Pago pendiente de procesar */
    PENDIENTE,
    
    /** Pago completado exitosamente */
    COMPLETADO,
    
    /** Pago que fue rechazado por el procesador de pagos */
    RECHAZADO,
    
    /** Pago que ha sido reembolsado al cliente */
    REEMBOLSADO,
    
    /** Pago que fue cancelado antes de ser procesado */
    CANCELADO,
    
    /** Pago que falló durante el procesamiento */
    FALLIDO
}
