import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { Auth } from '../../services/auth';

@Component({
  selector: 'app-verify-email',
  imports: [FormsModule, NgIf, RouterLink],
  templateUrl: './verify-email.html',
  styleUrl: './verify-email.css',
})
export class VerifyEmail implements OnInit {
  email = '';
  code = '';
  rol = '';

  errorMessage = '';
  successMessage = '';

  isLoading = false;
  isResending = false;

  constructor(
    private auth: Auth,
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  ngOnInit(): void {
    const emailFromUrl = this.route.snapshot.queryParamMap.get('email');

    const roleFromUrl = this.route.snapshot.queryParamMap.get('rol');

    const savedEmail = this.auth.getVerificationEmail();

    const savedRole = localStorage.getItem('pendingRegistrationRole');

    if (emailFromUrl) {
      this.email = emailFromUrl.trim().toLowerCase();
    } else if (savedEmail) {
      this.email = savedEmail.trim().toLowerCase();
    }

    if (roleFromUrl) {
      this.rol = roleFromUrl.trim().toUpperCase();

      localStorage.setItem('pendingRegistrationRole', this.rol);
    } else if (savedRole) {
      this.rol = savedRole.trim().toUpperCase();
    }

    if (this.email) {
      this.auth.saveVerificationEmail(this.email);
    }
  }

  onVerify(): void {
    this.errorMessage = '';
    this.successMessage = '';

    const email = this.email.trim().toLowerCase();

    const code = this.code.trim();

    if (!email || !code) {
      this.errorMessage = 'Ingresa tu correo y el código de verificación';
      return;
    }

    if (!this.isValidEmail(email)) {
      this.errorMessage = 'Ingresa un correo electrónico válido';
      return;
    }

    if (!/^\d{6}$/.test(code)) {
      this.errorMessage = 'El código debe contener 6 dígitos';
      return;
    }

    this.isLoading = true;

    this.auth
      .verifyEmail({
        email,
        codigo: code,
      })
      .subscribe({
        next: (response) => {
          console.log('Correo verificado:', response);

          this.isLoading = false;

          this.auth.clearVerificationEmail();

          const pendingRole = localStorage.getItem('pendingRegistrationRole')?.trim().toUpperCase();

          const redirectUrl = pendingRole === 'DRIVER' ? '/driver-registration' : '/passenger';

          this.successMessage =
            'Cuenta verificada correctamente. Redirigiendo al inicio de sesión...';

          setTimeout(() => {
            this.router.navigate(['/login'], {
              queryParams: {
                redirect: redirectUrl,
              },
            });
          }, 1200);
        },

        error: (err) => {
          console.error('Error al verificar correo:', err);

          this.isLoading = false;

          if (err.status === 400) {
            this.errorMessage = err.error?.message || 'El código es incorrecto o ha expirado';
          } else if (err.status === 404) {
            this.errorMessage = 'No se encontró una cuenta con ese correo';
          } else if (err.status === 409) {
            this.errorMessage = err.error?.message || 'La cuenta ya fue verificada';
          } else if (err.status === 0) {
            this.errorMessage = 'No se pudo conectar con el servidor';
          } else {
            this.errorMessage = err.error?.message || 'No se pudo verificar la cuenta';
          }
        },
      });
  }

  resendCode(): void {
    this.errorMessage = '';
    this.successMessage = '';

    const email = this.email.trim().toLowerCase();

    if (!email) {
      this.errorMessage = 'Ingresa tu correo electrónico';
      return;
    }

    if (!this.isValidEmail(email)) {
      this.errorMessage = 'Ingresa un correo electrónico válido';
      return;
    }

    this.isResending = true;

    this.auth
      .resendCode({
        email,
      })
      .subscribe({
        next: (response) => {
          console.log('Código reenviado:', response);

          this.isResending = false;

          this.successMessage = response.message || 'Se envió un nuevo código a tu correo';
        },

        error: (err) => {
          console.error('Error al reenviar código:', err);

          this.isResending = false;

          if (err.status === 400) {
            this.errorMessage = err.error?.message || 'No se pudo generar un nuevo código';
          } else if (err.status === 404) {
            this.errorMessage = 'No se encontró una cuenta con ese correo';
          } else if (err.status === 409) {
            this.errorMessage = err.error?.message || 'La cuenta ya se encuentra verificada';
          } else if (err.status === 0) {
            this.errorMessage = 'No se pudo conectar con el servidor';
          } else {
            this.errorMessage = err.error?.message || 'No se pudo reenviar el código';
          }
        },
      });
  }

  onCodeInput(event: Event): void {
    const input = event.target as HTMLInputElement;

    const sanitizedValue = input.value.replace(/\D/g, '').slice(0, 6);

    input.value = sanitizedValue;

    this.code = sanitizedValue;
  }

  private isValidEmail(email: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  }
}
