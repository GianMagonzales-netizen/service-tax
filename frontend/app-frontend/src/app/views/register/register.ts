import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { Router, RouterLink } from '@angular/router';

import { Auth } from '../../services/auth';

@Component({
  selector: 'app-register',
  imports: [FormsModule, NgIf, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  nombre = '';
  apellido = '';
  telefono = '';
  email = '';
  password = '';
  confirmPassword = '';
  rol = 'CLIENT';

  errorMessage = '';
  successMessage = '';
  isLoading = false;

  constructor(
    private auth: Auth,
    private router: Router,
  ) {}

  onRegister(): void {
    this.errorMessage = '';
    this.successMessage = '';

    const nombre = this.nombre.trim();
    const apellido = this.apellido.trim();
    const telefono = this.telefono.trim();
    const email = this.email.trim().toLowerCase();
    const password = this.password;
    const rol = this.rol.trim().toUpperCase();

    if (!nombre || !email || !password || !rol) {
      this.errorMessage = 'Completa todos los campos obligatorios';
      return;
    }

    if (!this.isValidEmail(email)) {
      this.errorMessage = 'Ingresa un correo electrónico válido';
      return;
    }

    if (password.length < 8) {
      this.errorMessage = 'La contraseña debe tener al menos 8 caracteres';
      return;
    }

    if (password !== this.confirmPassword) {
      this.errorMessage = 'Las contraseñas no coinciden';
      return;
    }

    if (telefono && !/^[0-9]{7,15}$/.test(telefono)) {
      this.errorMessage = 'Ingresa un número de teléfono válido';
      return;
    }

    if (rol !== 'CLIENT' && rol !== 'DRIVER') {
      this.errorMessage = 'Selecciona un tipo de cuenta válido';
      return;
    }

    this.isLoading = true;

    this.auth
      .register({
        nombre,
        apellido,
        telefono,
        email,
        password,
        rol,
      })
      .subscribe({
        next: (response) => {
          console.log('Registro exitoso:', response);

          this.isLoading = false;

          /*
           * Guardamos temporalmente el correo y el rol.
           * Se usarán después de verificar la cuenta.
           */
          this.auth.saveVerificationEmail(email);

          localStorage.setItem('pendingRegistrationRole', rol);

          this.successMessage =
            'Cuenta creada. Revisa tu correo para obtener el código de verificación.';

          setTimeout(() => {
            this.router.navigate(['/verify-email'], {
              queryParams: {
                email,
                rol,
              },
            });
          }, 800);
        },

        error: (err) => {
          console.error('Error en registro:', err);

          this.isLoading = false;

          if (err.status === 409) {
            this.errorMessage = 'Ya existe una cuenta con ese correo';
          } else if (err.status === 400) {
            this.errorMessage = this.getValidationMessage(err);
          } else if (err.status === 0) {
            this.errorMessage = 'No se pudo conectar con el servidor';
          } else {
            this.errorMessage = err.error?.message || 'No se pudo crear la cuenta';
          }
        },
      });
  }

  private isValidEmail(email: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  }

  private getValidationMessage(err: any): string {
    const error = err.error;

    if (typeof error === 'string') {
      return error;
    }

    if (error?.message) {
      return error.message;
    }

    if (error?.errors) {
      const errors = error.errors;

      if (Array.isArray(errors)) {
        return errors
          .map((item: any) => item.defaultMessage)
          .filter(Boolean)
          .join(', ');
      }

      if (typeof errors === 'object') {
        return Object.values(errors).join(', ');
      }
    }

    return 'Revisa los datos ingresados';
  }
}
