import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router } from '@angular/router';

import { Auth, UserRole } from '../services/auth';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const auth = inject(Auth);
  const router = inject(Router);

  // =====================================================
  // VALIDAR AUTENTICACIÓN
  // =====================================================

  if (!auth.isAuthenticated()) {
    auth.clearSession();

    return router.createUrlTree(['/login']);
  }

  // =====================================================
  // OBTENER ROL ACTUAL
  // =====================================================

  const currentRole = auth.getRol();

  if (!currentRole) {
    auth.clearSession();

    return router.createUrlTree(['/login']);
  }

  // =====================================================
  // OBTENER ROLES PERMITIDOS EN LA RUTA
  // =====================================================

  const allowedRoles = (route.data['roles'] as UserRole[] | undefined) ?? [];

  /*
   * Si la ruta no tiene roles configurados,
   * permitimos el acceso porque authGuard ya comprobó
   * que existe una sesión.
   */
  if (allowedRoles.length === 0) {
    return true;
  }

  // =====================================================
  // VALIDAR PERMISO
  // =====================================================

  if (allowedRoles.includes(currentRole)) {
    return true;
  }

  // =====================================================
  // REDIRECCIÓN SEGÚN EL ROL REAL
  // =====================================================

  switch (currentRole) {
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
