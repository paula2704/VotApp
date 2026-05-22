import { HttpInterceptorFn } from '@angular/common/http';

// interceptor funcional — intercepta TODAS las peticiones HTTP de la app
// su trabajo es agregar el token JWT al header Authorization automáticamente
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // lee el token guardado en localStorage después del login
  const token = localStorage.getItem('token');

  if (token) {
    // clona la petición agregando el header Authorization
    // se clona porque las peticiones HTTP son inmutables
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(authReq);
  }

  // si no hay token, deja pasar la petición sin modificar
  return next(req);
};