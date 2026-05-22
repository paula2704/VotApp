import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { AuthRequest, AuthResponse } from '../models/auth.model';

// @Injectable hace que Angular pueda inyectar este servicio en cualquier componente
// providedIn: 'root' significa que hay una sola instancia para toda la app (singleton)
@Injectable({ providedIn: 'root' })
export class AuthService {

  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient, private router: Router) {}

  login(request: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
      // tap ejecuta un efecto secundario sin modificar el valor del observable
      // aquí guardamos el token y el rol en localStorage después del login
      tap(response => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('role', response.role);
      })
    );
  }

  register(request: AuthRequest, role: 'USER' | 'ADMIN'): Observable<AuthResponse> {
    const endpoint = role === 'ADMIN' ? 'register/admin' : 'register/user';
    return this.http.post<AuthResponse>(`${this.apiUrl}/${endpoint}`, request).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('role', response.role);
      })
    );
  }

  logout(): void {
    // elimina el token y el rol del localStorage
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    this.router.navigate(['/login']);
  }

  // verifica si hay un token guardado
  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  // retorna el rol del usuario actual
  getRole(): string {
    return localStorage.getItem('role') || '';
  }

  isAdmin(): boolean {
    return this.getRole() === 'ADMIN';
  }
}