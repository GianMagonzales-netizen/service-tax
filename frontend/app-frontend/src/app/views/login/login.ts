import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { Router } from '@angular/router';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-login',
  imports: [FormsModule, NgIf],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  email = '';
  password = '';
  errorMessage = '';

  constructor(
    private auth: Auth,
    private router: Router,
  ) {}

  onLogin() {
    this.auth
      .login({
        email: this.email,
        password: this.password,
      })
      .subscribe({
        next: (response) => {
          localStorage.setItem('authMessage', response);
          this.router.navigate(['/dashboard']);
        },
        error: () => {
          this.errorMessage = 'Credenciales incorrectas';
        },
      });
  }
}
