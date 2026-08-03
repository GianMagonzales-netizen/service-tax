import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { Auth, UserRole } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login implements OnInit {
  email = '';
  password = '';

  errorMessage = '';
  isLoading = false;

  constructor(
    private readonly auth: Auth,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  // =====================================================
  // AL CARGAR EL LOGIN
  // =====================================================

  ngOnInit(): void {
    /*
     * Si ya existe una sesión válida,
     * enviamos al usuario a la vista correspondiente.
     */
    if (!this.auth.isAuthenticated()) {
      return;
    }

    const role = this.auth.getRol();

    if (!role) {
      this.auth.clearSession();
      return;
    }

    this.redirectByRole(role);
  }

  // =====================================================
  // INICIAR SESIÓN
  // =====================================================

  onLogin(): void {
    if (this.isLoading) {
      return;
    }

    this.errorMessage = '';

    const email = this.email.trim().toLowerCase();

    const password = this.password.trim();

    if (!email || !password) {
      this.errorMessage = 'Completa el correo y la contraseña';

      return;
    }

    this.isLoading = true;

    this.auth
      .login({
        email,
        password,
      })
      .subscribe({
        next: (response) => {
          console.log('Respuesta completa del login:', response);

          if (!response?.token) {
            this.auth.clearSession();

            this.errorMessage = 'El servidor no devolvió un token válido';

            this.isLoading = false;
            return;
          }

          const role = this.normalizeRole(response.rol);

          if (!role) {
            this.auth.clearSession();

            this.errorMessage = 'El usuario tiene un rol no reconocido';

            this.isLoading = false;
            return;
          }

          const requestedRedirect = this.route.snapshot.queryParamMap.get('redirect');

          const pendingRole =
            localStorage.getItem('pendingRegistrationRole')?.trim().toUpperCase() ?? null;

          console.log('Login exitoso:', {
            userId: response.userId,
            email: response.email,
            nombre: response.nombre,
            rol: role,
            requestedRedirect,
            pendingRole,
          });

          this.isLoading = false;

          /*
           * La sesión ya fue guardada automáticamente
           * por Auth.login() mediante tap().
           */
          localStorage.removeItem('pendingRegistrationRole');

          /*
           * Solo permitimos redirecciones internas
           * conocidas y compatibles con el rol real.
           */
          if (this.isAllowedRedirect(requestedRedirect, role)) {
            this.router.navigateByUrl(requestedRedirect as string);

            return;
          }

          /*
           * Si no existe una redirección válida,
           * enviamos al usuario según su rol.
           */
          this.redirectByRole(role);
        },

        error: (error) => {
          console.error('Error en login:', error);

          this.isLoading = false;

          /*
           * Eliminamos cualquier posible sesión anterior
           * para evitar accesos con datos desactualizados.
           */
          this.auth.clearSession();

          if (error.status === 401 || error.status === 403) {
            this.errorMessage = error.error?.message || 'Correo o contraseña incorrectos';

            return;
          }

          if (error.status === 400) {
            this.errorMessage = error.error?.message || 'Revisa los datos ingresados';

            return;
          }

          if (error.status === 0) {
            this.errorMessage = 'No se pudo conectar con el servidor';

            return;
          }

          this.errorMessage = error.error?.message || 'Ocurrió un error al iniciar sesión';
        },
      });
  }

  // =====================================================
  // REDIRECCIÓN SEGÚN EL ROL
  // =====================================================

  private redirectByRole(role: UserRole): void {
    switch (role) {
      case 'ADMIN':
        this.router.navigate(['/admin']);
        return;

      case 'DRIVER':
        this.router.navigate(['/driver-registration']);
        return;

      case 'CLIENT':
        this.router.navigate(['/passenger']);
        return;

      default:
        this.auth.clearSession();

        this.errorMessage = 'No se encontró una vista válida para el usuario';
    }
  }

  // =====================================================
  // VALIDAR REDIRECCIÓN SOLICITADA
  // =====================================================

  private isAllowedRedirect(redirect: string | null, role: UserRole): boolean {
    if (!redirect) {
      return false;
    }

    const allowedRedirects: Record<UserRole, string[]> = {
      ADMIN: ['/admin'],

      DRIVER: ['/driver-registration'],

      CLIENT: ['/passenger'],
    };

    return allowedRedirects[role].includes(redirect);
  }

  // =====================================================
  // NORMALIZAR ROL DEL BACKEND
  // =====================================================

  private normalizeRole(role: string | null | undefined): UserRole | null {
    const normalizedRole = role?.trim().toUpperCase();

    if (normalizedRole === 'ADMIN' || normalizedRole === 'DRIVER' || normalizedRole === 'CLIENT') {
      return normalizedRole;
    }

    return null;
  }
}
