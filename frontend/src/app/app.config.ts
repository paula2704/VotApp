import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { authInterceptor } from './interceptors/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    // provideHttpClient registra el cliente HTTP para toda la app
    // withInterceptors agrega nuestro interceptor JWT automáticamente
    provideHttpClient(withInterceptors([authInterceptor]))
  ]
};