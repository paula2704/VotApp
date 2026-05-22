import { Routes } from '@angular/router';
import { authGuard, adminGuard } from './guards/auth.guard';

export const routes: Routes = [
  // ruta raíz redirige al login
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  // rutas públicas
  { path: 'login', loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./pages/register/register.component').then(m => m.RegisterComponent) },

  // rutas protegidas — requieren login
  { path: 'surveys', loadComponent: () => import('./pages/surveys/surveys.component').then(m => m.SurveysComponent), canActivate: [authGuard] },

  // rutas solo para admin
  { path: 'admin', loadComponent: () => import('./pages/admin/admin.component').then(m => m.AdminComponent), canActivate: [authGuard, adminGuard] },

  // cualquier ruta desconocida redirige al login
  { path: '**', redirectTo: 'login' }
];