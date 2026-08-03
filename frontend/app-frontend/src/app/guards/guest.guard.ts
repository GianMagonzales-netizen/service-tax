import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { Auth } from '../services/auth';

export const guestGuard: CanActivateFn = () => {
  const auth = inject(Auth);
  const router = inject(Router);

  // Si NO ha iniciado sesión, puede acceder al login o registro.
  if (!auth.isAuthenticated()) {
    return true;
  }

  const role = auth.getRol();

  switch (role) {
    case 'ADMIN':
      return router.createUrlTree(['/admin']);

    case 'DRIVER':
      return router.createUrlTree(['/driver-registration']);

    case 'CLIENT':
      return router.createUrlTree(['/passenger']);

    default:
      auth.clearSession();
      return router.createUrlTree(['/login']);
  }
};
