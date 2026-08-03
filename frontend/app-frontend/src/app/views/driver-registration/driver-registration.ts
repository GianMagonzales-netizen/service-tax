import { CommonModule } from '@angular/common';

import { Component, OnInit } from '@angular/core';

import { FormsModule } from '@angular/forms';

import { Router } from '@angular/router';

import { Auth } from '../../services/auth';

import { Driver, DriverRequest, ServiceType } from '../../services/driver';

@Component({
  selector: 'app-driver-registration',
  standalone: true,

  imports: [CommonModule, FormsModule],

  templateUrl: './driver-registration.html',
  styleUrl: './driver-registration.css',
})
export class DriverRegistration implements OnInit {
  name = '';
  phone = '';

  vehiclePlate = '';
  vehicleModel = '';

  serviceType: ServiceType = 'STANDARD';

  latitude: number | null = null;
  longitude: number | null = null;

  errorMessage = '';
  successMessage = '';

  isLoading = false;
  isGettingLocation = false;

  // Evita utilizar el formulario mientras se verifica
  // si el conductor ya está registrado.
  isCheckingDriver = true;

  constructor(
    private readonly auth: Auth,
    private readonly driverService: Driver,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    const userId = this.auth.getUserId();

    const role = this.auth.getRol()?.trim().toUpperCase();

    // No existe una sesión válida
    if (!userId) {
      this.auth.clearSession();
      this.router.navigate(['/login']);
      return;
    }

    // Solo los usuarios DRIVER pueden entrar
    if (role !== 'DRIVER') {
      this.router.navigate([this.auth.getHomeRouteByRole()]);
      return;
    }

    const nombre = this.auth.getNombre();

    if (nombre) {
      this.name = nombre;
    }

    // Comprueba si ya existe un conductor asociado al usuario
    this.checkExistingDriver(userId);
  }

  /**
   * Verifica si el usuario ya tiene un perfil en la tabla drivers.
   *
   * - Si existe: bloquea el registro y redirige a /driver.
   * - Si devuelve 404: permite completar el formulario.
   */
  private checkExistingDriver(userId: number): void {
    this.isCheckingDriver = true;
    this.errorMessage = '';

    this.driverService.getDriverByUserId(userId).subscribe({
      next: (driver) => {
        console.log('El usuario ya tiene perfil de conductor:', driver);

        if (driver?.id) {
          localStorage.setItem('driverId', driver.id.toString());
        }

        this.errorMessage = 'Este usuario ya está registrado como conductor';

        this.isCheckingDriver = false;

        setTimeout(() => {
          this.router.navigate(['/driver']);
        }, 900);
      },

      error: (error) => {
        console.log('Resultado de verificación del conductor:', error);

        this.isCheckingDriver = false;

        // 404 significa que todavía no existe perfil.
        // Por eso puede continuar con el registro.
        if (error.status === 404) {
          return;
        }

        if (error.status === 401 || error.status === 403) {
          this.auth.clearSession();

          this.router.navigate(['/login']);
          return;
        }

        if (error.status === 0) {
          this.errorMessage = 'No se pudo conectar con el servidor';
          return;
        }

        this.errorMessage = error.error?.message || 'No se pudo verificar el perfil del conductor';
      },
    });
  }

  onSubmit(): void {
    if (this.isLoading || this.isCheckingDriver) {
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';

    const userId = this.auth.getUserId();

    const role = this.auth.getRol()?.trim().toUpperCase();

    const name = this.name.trim();
    const phone = this.phone.trim();

    const vehiclePlate = this.vehiclePlate.trim().toUpperCase();

    const vehicleModel = this.vehicleModel.trim();

    if (!userId) {
      this.errorMessage = 'No se encontró el usuario autenticado';

      this.auth.clearSession();

      setTimeout(() => {
        this.router.navigate(['/login']);
      }, 800);

      return;
    }

    if (role !== 'DRIVER') {
      this.errorMessage = 'Esta cuenta no tiene el rol de conductor';
      return;
    }

    if (!name || !vehiclePlate || !vehicleModel || !this.serviceType) {
      this.errorMessage = 'Completa todos los campos obligatorios';
      return;
    }

    if (name.length < 3 || name.length > 100) {
      this.errorMessage = 'El nombre debe tener entre 3 y 100 caracteres';
      return;
    }

    if (!/^[A-Z0-9]{3}-[A-Z0-9]{3}$/.test(vehiclePlate)) {
      this.errorMessage = 'La placa debe tener el formato ABC-123';
      return;
    }

    if (phone && !/^[0-9]{9,15}$/.test(phone)) {
      this.errorMessage = 'El teléfono debe tener entre 9 y 15 números';
      return;
    }

    if (this.latitude === null || this.longitude === null) {
      this.errorMessage = 'Debes registrar una ubicación';
      return;
    }

    const request: DriverRequest = {
      userId,
      name,
      vehiclePlate,
      vehicleModel,
      serviceType: this.serviceType,
      phone: phone || undefined,
      latitude: this.latitude,
      longitude: this.longitude,
    };

    this.isLoading = true;

    this.driverService.registerDriver(request).subscribe({
      next: (response) => {
        console.log('Conductor registrado:', response);

        this.isLoading = false;

        localStorage.setItem('driverId', response.id.toString());

        this.successMessage = 'Perfil de conductor registrado correctamente';

        /*
         * Espera un momento para mostrar el mensaje,
         * elimina la sesión y vuelve al login.
         */
        setTimeout(() => {
          this.auth.clearSession();

          localStorage.removeItem('driverId');

          this.router.navigate(['/login']);
        }, 1200);
      },

      error: (error) => {
        console.error('Error al registrar conductor:', error);

        this.isLoading = false;

        const backendMessage = this.extractErrorMessage(error);

        const normalizedMessage = backendMessage.toLowerCase();

        /*
         * Esta validación debe estar antes del status 400,
         * porque el backend podría devolver el duplicado
         * también como Bad Request.
         */
        if (
          error.status === 409 ||
          normalizedMessage.includes('already registered') ||
          normalizedMessage.includes('ya está registrado')
        ) {
          this.errorMessage = 'Este usuario ya está registrado como conductor';

          this.isLoading = false;

          return;
        }

        if (error.status === 400) {
          this.errorMessage = this.getValidationMessage(error);
          return;
        }

        if (error.status === 401 || error.status === 403) {
          this.errorMessage = 'Tu sesión expiró o no tiene permiso';

          setTimeout(() => {
            this.auth.clearSession();
            this.router.navigate(['/login']);
          }, 900);

          return;
        }

        if (error.status === 0) {
          this.errorMessage = 'No se pudo conectar con el servidor';
          return;
        }

        this.errorMessage = backendMessage || 'No se pudo registrar el conductor';
      },
    });
  }

  formatPlate(): void {
    let value = this.vehiclePlate
      .toUpperCase()
      .replace(/[^A-Z0-9]/g, '')
      .slice(0, 6);

    if (value.length > 3) {
      value = `${value.slice(0, 3)}-${value.slice(3)}`;
    }

    this.vehiclePlate = value;
  }

  getCurrentLocation(): void {
    this.errorMessage = '';

    if (!navigator.geolocation) {
      this.errorMessage = 'Tu navegador no permite obtener la ubicación';
      return;
    }

    this.isGettingLocation = true;

    navigator.geolocation.getCurrentPosition(
      (position) => {
        this.latitude = Number(position.coords.latitude.toFixed(6));

        this.longitude = Number(position.coords.longitude.toFixed(6));

        this.isGettingLocation = false;
      },

      (error) => {
        console.error('Error de ubicación:', error);

        this.isGettingLocation = false;

        this.errorMessage = 'No se pudo obtener tu ubicación. Revisa los permisos del navegador';
      },

      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 0,
      },
    );
  }

  private extractErrorMessage(error: any): string {
    if (typeof error?.error === 'string') {
      return error.error;
    }

    if (error?.error?.message) {
      return error.error.message;
    }

    if (error?.message) {
      return error.message;
    }

    return '';
  }

  private getValidationMessage(error: any): string {
    const body = error.error;

    if (typeof body === 'string') {
      return body;
    }

    if (body?.message) {
      return body.message;
    }

    if (body?.errors && typeof body.errors === 'object') {
      if (Array.isArray(body.errors)) {
        return body.errors
          .map((item: any) => item.defaultMessage || item.message)
          .filter(Boolean)
          .join(', ');
      }

      return Object.values(body.errors).join(', ');
    }

    return 'Revisa los datos ingresados';
  }
}
