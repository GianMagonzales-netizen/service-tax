import { Component, OnInit } from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { Auth } from '../../services/auth';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './reset-password.html',
  styleUrl: './reset-password.css',
})
export class ResetPassword implements OnInit {
  email = '';
  codigo = '';

  nuevaPassword = '';
  confirmarPassword = '';

  showPassword = false;
  showConfirmPassword = false;

  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private auth: Auth,
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  ngOnInit(): void {
    const queryEmail = this.route.snapshot.queryParamMap.get('email');

    const savedEmail = localStorage.getItem('passwordRecoveryEmail');

    this.email = queryEmail || savedEmail || '';

    if (!this.email) {
      this.router.navigate(['/forgot-password']);
    }
  }

  resetPassword(): void {
    this.errorMessage = '';
    this.successMessage = '';

    const cleanCode = this.codigo.trim();

    if (!/^\d{6}$/.test(cleanCode)) {
      this.errorMessage = 'El código debe contener exactamente 6 números';

      return;
    }

    if (this.nuevaPassword.length < 8) {
      this.errorMessage = 'La contraseña debe tener al menos 8 caracteres';

      return;
    }

    if (this.nuevaPassword !== this.confirmarPassword) {
      this.errorMessage = 'Las contraseñas no coinciden';

      return;
    }

    this.loading = true;

    this.auth
      .resetPassword({
        email: this.email,
        codigo: cleanCode,
        nuevaPassword: this.nuevaPassword,
      })
      .subscribe({
        next: (response) => {
          this.loading = false;

          this.successMessage = response.message || 'Contraseña actualizada correctamente';

          localStorage.removeItem('passwordRecoveryEmail');

          setTimeout(() => {
            this.router.navigate(['/login'], {
              queryParams: {
                passwordReset: true,
              },
            });
          }, 1500);
        },

        error: (error) => {
          this.loading = false;

          console.error('Error al cambiar contraseña:', error);

          this.errorMessage =
            error.error?.message || error.error?.error || 'No se pudo cambiar la contraseña';
        },
      });
  }

  resendCode(): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.auth
      .forgotPassword({
        email: this.email,
      })
      .subscribe({
        next: (response) => {
          this.successMessage = response.message || 'Código reenviado correctamente';
        },

        error: (error) => {
          this.errorMessage =
            error.error?.message || error.error?.error || 'No se pudo reenviar el código';
        },
      });
  }

  onlyNumbers(event: Event): void {
    const input = event.target as HTMLInputElement;

    input.value = input.value.replace(/\D/g, '');

    this.codigo = input.value;
  }
}
