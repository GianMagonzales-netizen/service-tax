import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { RideRequestComponent } from '../components/passenger/ride-request/ride-request';
import { LoadingOverlayComponent } from '../components/passenger/loading-overlay/loading-overlay';
import { TripCompletedComponent } from '../components/passenger/trip-completed/trip-completed';

import { RideRequest } from '../../models/ride-request.model';
import { OrchestratorResponse } from '../../models/orchestrator-response.model';

import { OrchestratorService } from '../../services/orchestrator.service';
import { Auth } from '../../services/auth';

type PassengerStep = 'REQUEST' | 'COMPLETED';

interface HttpErrorResponseBody {
  message?: string;
}

interface HttpErrorLike {
  error?: HttpErrorResponseBody | string;
  message?: string;
}

@Component({
  selector: 'app-passenger',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    RideRequestComponent,
    LoadingOverlayComponent,
    TripCompletedComponent,
  ],
  templateUrl: './passenger.html',
  styleUrl: './passenger.css',
})
export class Passenger {
  currentStep: PassengerStep = 'REQUEST';

  isLoading = false;
  errorMessage = '';

  rideResponse: OrchestratorResponse | null = null;

  constructor(
    private readonly orchestratorService: OrchestratorService,
    private readonly auth: Auth,
    private readonly router: Router,
  ) {}

  handleRideRequested(request: RideRequest): void {
    if (this.isLoading) {
      return;
    }

    this.errorMessage = '';
    this.rideResponse = null;
    this.isLoading = true;

    this.orchestratorService
      .requestRide(request)
      .pipe(
        finalize(() => {
          this.isLoading = false;
        }),
      )
      .subscribe({
        next: (response: OrchestratorResponse) => {
          if (!response.success) {
            this.errorMessage = response.message?.trim() || 'No se pudo procesar el viaje';

            return;
          }

          this.rideResponse = response;
          this.currentStep = 'COMPLETED';
        },

        error: (error: unknown) => {
          console.error('Error al solicitar el viaje:', error);

          this.errorMessage = this.extractErrorMessage(error);
        },
      });
  }

  startNewRide(): void {
    this.currentStep = 'REQUEST';
    this.rideResponse = null;
    this.errorMessage = '';
    this.isLoading = false;
  }

  logout(): void {
    this.auth.clearSession();
    this.router.navigate(['/login']);
  }

  private extractErrorMessage(error: unknown): string {
    if (typeof error !== 'object' || error === null) {
      return 'No fue posible conectar con el servidor';
    }

    const httpError = error as HttpErrorLike;

    if (
      typeof httpError.error === 'object' &&
      httpError.error !== null &&
      httpError.error.message?.trim()
    ) {
      return httpError.error.message.trim();
    }

    if (typeof httpError.error === 'string' && httpError.error.trim()) {
      return httpError.error.trim();
    }

    if (httpError.message?.trim()) {
      return httpError.message.trim();
    }

    return 'No fue posible conectar con el servidor';
  }
}
