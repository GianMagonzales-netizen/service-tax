import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { PaymentMethod, RideRequest, ServiceType } from '../../../../models/ride-request.model';

@Component({
  selector: 'app-ride-request',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ride-request.html',
  styleUrl: './ride-request.css',
})
export class RideRequestComponent {
  @Output()
  rideRequested = new EventEmitter<RideRequest>();

  request: RideRequest = {
    // Temporal: luego se obtendrá del usuario autenticado.
    clientId: 1,

    serviceType: 'STANDARD',

    pickupLat: -12.0775,
    pickupLng: -77.0837,
    pickupAddress: '',

    destinationLat: -12.0464,
    destinationLng: -77.0428,
    destinationAddress: '',

    // ✅ CAMBIADO
    paymentMethod: 'EFECTIVO',
  };

  serviceOptions: Array<{
    value: ServiceType;
    name: string;
    description: string;
  }> = [
    {
      value: 'STANDARD',
      name: 'Standard',
      description: 'Viaje económico y cómodo',
    },
    {
      value: 'CONFORT',
      name: 'Confort',
      description: 'Vehículo con mayor comodidad',
    },
  ];

  paymentOptions: Array<{
    value: PaymentMethod;
    name: string;
  }> = [
    {
      value: 'EFECTIVO',
      name: 'Efectivo',
    },
    {
      value: 'TARJETA',
      name: 'Tarjeta',
    },
    {
      value: 'YAPE',
      name: 'Yape',
    },
    {
      value: 'PLIN',
      name: 'Plin',
    },
  ];

  selectService(serviceType: ServiceType): void {
    this.request.serviceType = serviceType;
  }

  submitRide(): void {
    if (!this.isFormValid()) {
      return;
    }

    this.rideRequested.emit({
      ...this.request,
      pickupAddress: this.request.pickupAddress?.trim(),
      destinationAddress: this.request.destinationAddress?.trim(),
    });
  }

  private isFormValid(): boolean {
    const pickupAddress = this.request.pickupAddress?.trim() ?? '';

    const destinationAddress = this.request.destinationAddress?.trim() ?? '';

    return pickupAddress.length > 0 && destinationAddress.length > 0;
  }
}
