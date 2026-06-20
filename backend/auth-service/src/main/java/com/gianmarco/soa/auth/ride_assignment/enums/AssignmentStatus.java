package com.gianmarco.soa.auth.ride_assignment.enums;

public enum AssignmentStatus {
    PENDING,    // Esperando respuesta del conductor
    ACCEPTED,   // Conductor aceptó
    REJECTED,   // Conductor rechazó
    COMPLETED,  // Viaje completado
    EXPIRED     // Expiró sin respuesta
}