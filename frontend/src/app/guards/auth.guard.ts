import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

// guard funcional — protege rutas que requieren autenticación
// si el usuario no está logueado, lo redirige al login
export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn()) {
    return true; // deja pasar
  }

  router.navigate(['/login']);
  return false; // bloquea el acceso
};

// guard para rutas que solo puede ver el admin
export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAdmin()) {
    return true;
  }

  router.navigate(['/surveys']);
  return false;
};