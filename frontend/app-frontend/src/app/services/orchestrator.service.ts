import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { RideRequest } from '../models/ride-request.model';
import { OrchestratorResponse } from '../models/orchestrator-response.model';
import { TripStatus } from '../models/trip-status.model';

@Injectable({
  providedIn: 'root',
})
export class OrchestratorService {
  private readonly apiUrl = 'http://localhost:8080/api/orchestrator';

  constructor(private http: HttpClient) {}

  // =====================================================
  // SOLICITAR VIAJE
  // =====================================================

  requestRide(request: RideRequest): Observable<OrchestratorResponse> {
    return this.http.post<OrchestratorResponse>(`${this.apiUrl}/request-ride`, request);
  }

  // =====================================================
  // REASIGNAR CONDUCTOR
  // =====================================================

  reassignRide(rideRequestId: number): Observable<OrchestratorResponse> {
    return this.http.post<OrchestratorResponse>(`${this.apiUrl}/reassign/${rideRequestId}`, null);
  }

  // =====================================================
  // INICIAR VIAJE
  // Usa el endpoint existente de aceptación
  // =====================================================

  startRide(rideRequestId: number, driverId: number): Observable<OrchestratorResponse> {
    const params = new HttpParams().set('driverId', driverId.toString());

    return this.http.post<OrchestratorResponse>(
      `${this.apiUrl}/${rideRequestId}/driver/accept`,
      null,
      { params },
    );
  }

  // =====================================================
  // ACEPTAR VIAJE
  // Se conserva por compatibilidad
  // =====================================================

  acceptRide(rideRequestId: number, driverId: number): Observable<OrchestratorResponse> {
    const params = new HttpParams().set('driverId', driverId.toString());

    return this.http.post<OrchestratorResponse>(
      `${this.apiUrl}/${rideRequestId}/driver/accept`,
      null,
      { params },
    );
  }

  // =====================================================
  // RECHAZAR VIAJE
  // =====================================================

  rejectRide(
    rideRequestId: number,
    driverId: number,
    reason?: string,
  ): Observable<OrchestratorResponse> {
    let params = new HttpParams().set('driverId', driverId.toString());

    if (reason?.trim()) {
      params = params.set('reason', reason.trim());
    }

    return this.http.post<OrchestratorResponse>(
      `${this.apiUrl}/${rideRequestId}/driver/reject`,
      null,
      { params },
    );
  }

  // =====================================================
  // FINALIZAR VIAJE
  // =====================================================

  completeRide(rideRequestId: number, driverId: number): Observable<OrchestratorResponse> {
    const params = new HttpParams().set('driverId', driverId.toString());

    return this.http.post<OrchestratorResponse>(`${this.apiUrl}/${rideRequestId}/complete`, null, {
      params,
    });
  }

  // =====================================================
  // CANCELAR VIAJE
  // =====================================================

  cancelRide(rideRequestId: number, clientId: number): Observable<OrchestratorResponse> {
    const params = new HttpParams().set('clientId', clientId.toString());

    return this.http.post<OrchestratorResponse>(`${this.apiUrl}/${rideRequestId}/cancel`, null, {
      params,
    });
  }

  // =====================================================
  // OBTENER INFORMACIÓN COMPLETA
  // =====================================================

  getCompleteStatus(rideRequestId: number): Observable<TripStatus> {
    return this.http.get<TripStatus>(`${this.apiUrl}/status/${rideRequestId}`);
  }

  // =====================================================
  // OBTENER ESTADO SIMPLE
  // =====================================================

  getSimpleStatus(rideRequestId: number): Observable<string> {
    return this.http.get(`${this.apiUrl}/${rideRequestId}/status`, {
      responseType: 'text',
    });
  }
}
