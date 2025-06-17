package com.tickets.ravetix.util;

import com.tickets.ravetix.entity.Event;
import com.tickets.ravetix.entity.Zone;
import com.tickets.ravetix.enums.TicketState;

public class EventStatisticsCalculator {
    
    private EventStatisticsCalculator() {
        // Utility class
    }

    /**
     * Calcula las estadísticas de entradas para un evento basado en sus tickets.
     * @param event El evento para calcular las estadísticas.
     */
    public static void calculateEventStatistics(Event event) {
        long totalTickets = event.getZonas().stream()
            .flatMap(zone -> zone.getTickets().stream())
            .count();
            
        long soldTickets = event.getZonas().stream()
            .flatMap(zone -> zone.getTickets().stream())
            .filter(ticket -> ticket.getEstado() == TicketState.PAGADO)
            .count();
            
        event.setEntradasVendidas((int) soldTickets);
        event.setEntradasDisponibles(event.getCapacidadTotal() - (int) soldTickets);

    }

    /**
     * Calcula las estadísticas de entradas para una zona basada en sus tickets.
     * @param zone La zona para calcular las estadísticas.
     */
    public static void calculateZoneStatistics(Zone zone) {
        long soldTickets = zone.getTickets().stream()
            .filter(ticket -> ticket.getEstado() == TicketState.PAGADO)
            .count();

        zone.setEntradasVendidas((int) soldTickets);
        zone.setEntradasDisponibles((int) (zone.getCapacidad() - soldTickets));
    }
}
