import { CommonModule, CurrencyPipe, DecimalPipe } from '@angular/common';

import { Component, EventEmitter, Input, Output } from '@angular/core';

import { OrchestratorResponse } from '../../../../models/orchestrator-response.model';
import { OrchestratorService } from '../../../../services/orchestrator.service';

@Component({
  selector: 'app-trip-completed',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, DecimalPipe],
  templateUrl: './trip-completed.html',
  styleUrl: './trip-completed.css',
})
export class TripCompletedComponent {
  @Input({ required: true })
  ride!: OrchestratorResponse;

  @Output()
  newRide = new EventEmitter<void>();

  currentStatus: 'DRIVER_ON_THE_WAY' | 'IN_PROGRESS' | 'PAYMENT' | 'COMPLETED' =
    'DRIVER_ON_THE_WAY';

  updatingStatus = false;
  statusError = false;

  constructor(private orchestratorService: OrchestratorService) {}

  get mainTitle(): string {
    switch (this.currentStatus) {
      case 'DRIVER_ON_THE_WAY':
        return 'Tu conductor está en camino';

      case 'IN_PROGRESS':
        return 'Viaje en curso';

      case 'PAYMENT':
        return 'Pago del viaje';

      case 'COMPLETED':
        return 'Viaje finalizado';

      default:
        return 'Tu conductor está en camino';
    }
  }

  get statusLabel(): string {
    switch (this.currentStatus) {
      case 'DRIVER_ON_THE_WAY':
        return 'Conductor en camino';

      case 'IN_PROGRESS':
        return 'Viaje en curso';

      case 'PAYMENT':
        return 'Pago pendiente';

      case 'COMPLETED':
        return 'Viaje finalizado';

      default:
        return 'Conductor en camino';
    }
  }

  get buttonLabel(): string {
    if (this.updatingStatus) {
      return 'Procesando...';
    }

    switch (this.currentStatus) {
      case 'DRIVER_ON_THE_WAY':
        return 'Iniciar viaje';

      case 'IN_PROGRESS':
        return 'Ir a pagar';

      case 'PAYMENT':
        return 'Confirmar pago';

      case 'COMPLETED':
        return 'Solicitar otro viaje';

      default:
        return 'Iniciar viaje';
    }
  }

  handleMainAction(): void {
    if (this.updatingStatus) {
      return;
    }

    this.statusError = false;

    switch (this.currentStatus) {
      case 'DRIVER_ON_THE_WAY':
        this.startRide();
        break;

      case 'IN_PROGRESS':
        this.goToPayment();
        break;

      case 'PAYMENT':
        this.finishRide();
        break;

      case 'COMPLETED':
        this.startNewRide();
        break;
    }
  }

  private startRide(): void {
    const rideRequestId = this.ride.rideRequestId;
    const driverId = this.ride.driverId;

    if (
      rideRequestId === null ||
      rideRequestId === undefined ||
      driverId === null ||
      driverId === undefined
    ) {
      console.error('No existe el ID del viaje o del conductor');

      this.statusError = true;
      return;
    }

    this.updatingStatus = true;
    this.statusError = false;

    this.orchestratorService.startRide(rideRequestId, driverId).subscribe({
      next: (response) => {
        this.ride = response;
        this.currentStatus = 'IN_PROGRESS';
        this.updatingStatus = false;
      },

      error: (error) => {
        console.error('Error iniciando el viaje:', error);

        this.statusError = true;
        this.updatingStatus = false;
      },
    });
  }

  private goToPayment(): void {
    this.statusError = false;
    this.currentStatus = 'PAYMENT';
  }

  private finishRide(): void {
    const rideRequestId = this.ride.rideRequestId;
    const driverId = this.ride.driverId;

    if (
      rideRequestId === null ||
      rideRequestId === undefined ||
      driverId === null ||
      driverId === undefined
    ) {
      console.error('No existe el ID del viaje o del conductor');

      this.statusError = true;
      return;
    }

    this.updatingStatus = true;
    this.statusError = false;

    this.orchestratorService.completeRide(rideRequestId, driverId).subscribe({
      next: (response) => {
        this.ride = response;
        this.currentStatus = 'COMPLETED';
        this.updatingStatus = false;
      },

      error: (error) => {
        console.error('Error procesando el pago y finalizando el viaje:', error);

        this.statusError = true;
        this.updatingStatus = false;
      },
    });
  }

  startNewRide(): void {
    this.currentStatus = 'DRIVER_ON_THE_WAY';
    this.statusError = false;
    this.updatingStatus = false;

    this.newRide.emit();
  }
}
