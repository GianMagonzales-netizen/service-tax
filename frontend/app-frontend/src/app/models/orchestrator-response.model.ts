import { ServiceType } from './ride-request.model';

export interface OrchestratorResponse {
  success: boolean;
  message: string;

  rideRequestId: number | null;
  assignmentId: number | null;
  paymentId: number | null;

  driverId: number | null;

  status: string | null;

  driverName: string | null;
  driverVehiclePlate: string | null;

  serviceType: ServiceType | null;

  estimatedFare: number | null;
  finalFare: number | null;

  transactionCode: string | null;

  paymentMethod: string | null;
  paymentStatus: string | null;

  distanceKm: number | null;
  estimatedTimeMinutes: number | null;
}
