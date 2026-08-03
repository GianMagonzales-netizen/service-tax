import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export type ServiceType = 'STANDARD' | 'CONFORT';

export type DriverStatus = 'AVAILABLE' | 'BUSY' | 'OFFLINE';

export interface DriverRequest {
  userId: number;
  name: string;
  vehiclePlate: string;
  vehicleModel: string;
  serviceType: ServiceType;
  phone?: string;
  latitude?: number;
  longitude?: number;
}

export interface DriverResponse {
  id: number;
  userId: number;
  name: string;
  vehiclePlate: string;
  vehicleModel: string;
  serviceType: ServiceType;
  status: DriverStatus;
  avgRating: number;
  completedRides: number;
  phone?: string;
  latitude?: number;
  longitude?: number;
  createdAt: string;
}

@Injectable({
  providedIn: 'root',
})
export class Driver {
  private readonly apiUrl = 'http://localhost:8080/api/drivers';

  constructor(private readonly http: HttpClient) {}

  registerDriver(data: DriverRequest): Observable<DriverResponse> {
    return this.http.post<DriverResponse>(this.apiUrl, data, {
      headers: this.getHeaders(),
    });
  }

  getDriverByUserId(userId: number): Observable<DriverResponse> {
    return this.http.get<DriverResponse>(`${this.apiUrl}/user/${userId}`, {
      headers: this.getHeaders(),
    });
  }

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');

    let headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });

    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }

    return headers;
  }
}
