import { ServiceType } from './ride-request.model';

export interface TripStatus {
  // Información de la solicitud
  rideRequest: unknown | null;
  rideStatus: string | null;

  createdAt: string | null;
  completedAt: string | null;

  // Información de la asignación
  currentAssignment: unknown | null;
  assignmentHistory: unknown[];

  driverName: string | null;
  driverVehiclePlate: string | null;

  serviceType: ServiceType | null;

  // Recorrido
  pickupLat: number | null;
  pickupLng: number | null;
  pickupAddress: string | null;

  destinationLat: number | null;
  destinationLng: number | null;
  destinationAddress: string | null;

  // Distancia y tarifa estimada
  distanceKm: number | null;
  estimatedFare: number | null;

  // Se utilizarán cuando el viaje termine
  finalFare: number | null;
  paymentMethod: string | null;
  transactionCode: string | null;

  // Estado actual
  currentStatus: string | null;
  lastUpdate: string | null;

  assignmentAttempts: number | null;

  // Hora estimada de llegada del conductor
  estimatedArrivalTime: string | null;
}
