import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { Auth } from '../../services/auth';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css',
})
export class ForgotPassword {
  email = '';

  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private auth: Auth,
    private router: Router,
  ) {}

  sendCode(): void {
    this.errorMessage = '';
    this.successMessage = '';

    const normalizedEmail = this.email.trim().toLowerCase();

    if (!normalizedEmail) {
      this.errorMessage = 'Ingresa tu correo electrónico';

      return;
    }

    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!emailPattern.test(normalizedEmail)) {
      this.errorMessage = 'Ingresa un correo electrónico válido';

      return;
    }

    this.loading = true;

    this.auth
      .forgotPassword({
        email: normalizedEmail,
      })
      .subscribe({
        next: (response) => {
          this.loading = false;

          this.successMessage = response.message || 'Código enviado correctamente';

          localStorage.setItem('passwordRecoveryEmail', normalizedEmail);

          setTimeout(() => {
            this.router.navigate(['/reset-password'], {
              queryParams: {
                email: normalizedEmail,
              },
            });
          }, 1200);
        },

        error: (error) => {
          this.loading = false;

          console.error('Error al solicitar recuperación:', error);

          this.errorMessage =
            error.error?.message || error.error?.error || 'No se pudo enviar el código';
        },
      });
  }
}
