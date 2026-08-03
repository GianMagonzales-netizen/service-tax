export type ServiceType = 'STANDARD' | 'CONFORT';

export type PaymentMethod = 'EFECTIVO' | 'TARJETA' | 'YAPE' | 'PLIN';

export interface RideRequest {
  clientId: number;

  serviceType: ServiceType;

  pickupLat: number;
  pickupLng: number;
  pickupAddress?: string;

  destinationLat: number;
  destinationLng: number;
  destinationAddress?: string;

  paymentMethod: PaymentMethod;
}
