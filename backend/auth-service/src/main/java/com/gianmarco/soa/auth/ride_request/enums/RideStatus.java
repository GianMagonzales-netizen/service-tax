package com.gianmarco.soa.auth.ride_request.enums;

public enum RideStatus {
    PENDING,     // Esperando asignación de conductor
    ASSIGNED,    // Conductor asignado, esperando confirmación
    IN_PROGRESS, // Conductor aceptó, viaje en curso
    COMPLETED,   // Viaje completado
    CANCELLED,   // Cancelado por cliente
    EXPIRED      // Expiró sin respuesta del conductor
}