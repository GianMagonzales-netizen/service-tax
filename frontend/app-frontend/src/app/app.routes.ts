import { Routes } from '@angular/router';

import { authGuard } from './guards/auth.guard';
import { guestGuard } from './guards/guest.guard';
import { roleGuard } from './guards/role.guard';

import { Login } from './views/login/login';
import { Register } from './views/register/register';
import { VerifyEmail } from './views/verify-email/verify-email';
import { DriverRegistration } from './views/driver-registration/driver-registration';
import { ForgotPassword } from './views/forgot-password/forgot-password';
import { ResetPassword } from './views/forgot-password/reset-password';
import { Passenger } from './views/passenger/passenger';
import { Admin } from './views/admin/admin';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full',
  },

  // =====================================================
  // RUTAS PÚBLICAS
  // =====================================================

  {
    path: 'login',
    component: Login,
    canActivate: [guestGuard],
  },

  {
    path: 'register',
    component: Register,
    canActivate: [guestGuard],
  },

  {
    path: 'verify-email',
    component: VerifyEmail,
    canActivate: [guestGuard],
  },

  {
    path: 'forgot-password',
    component: ForgotPassword,
    canActivate: [guestGuard],
  },

  {
    path: 'reset-password',
    component: ResetPassword,
    canActivate: [guestGuard],
  },

  // =====================================================
  // RUTA DEL CONDUCTOR
  // =====================================================

  {
    path: 'driver-registration',
    component: DriverRegistration,
    canActivate: [authGuard, roleGuard],
    data: {
      roles: ['DRIVER'],
    },
  },

  // =====================================================
  // RUTA DEL PASAJERO
  // =====================================================

  {
    path: 'passenger',
    component: Passenger,
    canActivate: [authGuard, roleGuard],
    data: {
      roles: ['CLIENT'],
    },
  },

  // =====================================================
  // RUTA DEL ADMINISTRADOR
  // =====================================================

  {
    path: 'admin',
    component: Admin,
    canActivate: [authGuard, roleGuard],
    data: {
      roles: ['ADMIN'],
    },
  },

  // =====================================================
  // RUTA NO ENCONTRADA
  // =====================================================

  {
    path: '**',
    redirectTo: 'login',
  },
];
