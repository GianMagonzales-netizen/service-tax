import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

// =====================================================
// ROLES DEL SISTEMA
// =====================================================

export type UserRole = 'CLIENT' | 'DRIVER' | 'ADMIN';

// =====================================================
// LOGIN
// =====================================================

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  userId: number;
  email: string;
  nombre: string;
  rol: UserRole;
}

// =====================================================
// REGISTRO
// =====================================================

export interface RegisterRequest {
  nombre: string;
  apellido: string;
  telefono: string;
  email: string;
  password: string;
  rol: UserRole;
}

export interface RegisterResponse {
  id: number;
  email: string;
  nombre: string;
  rol: UserRole;
  message?: string;
}

// =====================================================
// VERIFICACIÓN DE CORREO
// =====================================================

export interface VerifyEmailRequest {
  email: string;
  codigo: string;
}

export interface ResendCodeRequest {
  email: string;
}

// =====================================================
// RECUPERACIÓN DE CONTRASEÑA
// =====================================================

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  email: string;
  codigo: string;
  nuevaPassword: string;
}

// =====================================================
// RESPUESTAS GENERALES
// =====================================================

export interface MessageResponse {
  message: string;
}

@Injectable({
  providedIn: 'root',
})
export class Auth {
  private readonly apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  // =====================================================
  // LOGIN
  // =====================================================

  login(data: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, data).pipe(
      tap((response) => {
        this.saveSession(response);
      }),
    );
  }

  // =====================================================
  // REGISTRO
  // =====================================================

  register(data: RegisterRequest): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${this.apiUrl}/register`, data);
  }

  // =====================================================
  // VERIFICACIÓN DE CORREO
  // =====================================================

  verifyEmail(data: VerifyEmailRequest): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.apiUrl}/verify-email`, data);
  }

  resendCode(data: ResendCodeRequest): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.apiUrl}/resend-code`, data);
  }

  // =====================================================
  // RECUPERACIÓN DE CONTRASEÑA
  // =====================================================

  forgotPassword(data: ForgotPasswordRequest): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.apiUrl}/forgot-password`, data);
  }

  resetPassword(data: ResetPasswordRequest): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.apiUrl}/reset-password`, data);
  }

  // =====================================================
  // GUARDAR SESIÓN
  // =====================================================

  saveSession(response: LoginResponse): void {
    const role = this.normalizeRole(response.rol);

    localStorage.setItem('token', response.token);
    localStorage.setItem('userId', response.userId.toString());
    localStorage.setItem('email', response.email);
    localStorage.setItem('nombre', response.nombre ?? '');
    localStorage.setItem('rol', role);
  }

  // =====================================================
  // GUARDAR CORREO PENDIENTE DE VERIFICACIÓN
  // =====================================================

  saveVerificationEmail(email: string): void {
    localStorage.setItem('verificationEmail', email);
  }

  getVerificationEmail(): string | null {
    return localStorage.getItem('verificationEmail');
  }

  clearVerificationEmail(): void {
    localStorage.removeItem('verificationEmail');
  }

  // =====================================================
  // GUARDAR CORREO PARA RECUPERACIÓN
  // =====================================================

  savePasswordRecoveryEmail(email: string): void {
    localStorage.setItem('passwordRecoveryEmail', email);
  }

  getPasswordRecoveryEmail(): string | null {
    return localStorage.getItem('passwordRecoveryEmail');
  }

  clearPasswordRecoveryEmail(): void {
    localStorage.removeItem('passwordRecoveryEmail');
  }

  // =====================================================
  // OBTENER DATOS DE SESIÓN
  // =====================================================

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getUserId(): number | null {
    const userId = localStorage.getItem('userId');

    if (!userId) {
      return null;
    }

    const parsedUserId = Number(userId);

    return Number.isNaN(parsedUserId) ? null : parsedUserId;
  }

  getEmail(): string | null {
    return localStorage.getItem('email');
  }

  getNombre(): string | null {
    return localStorage.getItem('nombre');
  }

  getRol(): UserRole | null {
    const role = localStorage.getItem('rol');

    if (!role) {
      return null;
    }

    const normalizedRole = role.trim().toUpperCase();

    if (normalizedRole === 'CLIENT' || normalizedRole === 'DRIVER' || normalizedRole === 'ADMIN') {
      return normalizedRole;
    }

    return null;
  }

  // También puedes usar getRole() desde los guards.
  getRole(): UserRole | null {
    return this.getRol();
  }

  // =====================================================
  // COMPROBAR AUTENTICACIÓN
  // =====================================================

  isAuthenticated(): boolean {
    const token = this.getToken();

    return token !== null && token.trim() !== '';
  }

  // =====================================================
  // COMPROBAR ROLES
  // =====================================================

  hasRole(role: UserRole): boolean {
    return this.getRol() === role;
  }

  hasAnyRole(roles: UserRole[]): boolean {
    const currentRole = this.getRol();

    return currentRole !== null && roles.includes(currentRole);
  }

  isClient(): boolean {
    return this.hasRole('CLIENT');
  }

  isDriver(): boolean {
    return this.hasRole('DRIVER');
  }

  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }

  // =====================================================
  // OBTENER RUTA SEGÚN EL ROL
  // =====================================================

  getHomeRouteByRole(): string {
    switch (this.getRol()) {
      case 'ADMIN':
        return '/admin';

      case 'DRIVER':
        return '/driver';

      case 'CLIENT':
        return '/passenger';

      default:
        return '/login';
    }
  }

  // =====================================================
  // CERRAR SESIÓN
  // =====================================================

  logout(): void {
    this.clearSession();
  }

  clearSession(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('email');
    localStorage.removeItem('nombre');
    localStorage.removeItem('rol');

    this.clearVerificationEmail();
    this.clearPasswordRecoveryEmail();
  }

  // =====================================================
  // NORMALIZAR ROL
  // =====================================================

  private normalizeRole(role: string): UserRole {
    const normalizedRole = role?.trim().toUpperCase();

    if (normalizedRole === 'CLIENT' || normalizedRole === 'DRIVER' || normalizedRole === 'ADMIN') {
      return normalizedRole;
    }

    throw new Error(`Rol no válido: ${role}`);
  }
}
