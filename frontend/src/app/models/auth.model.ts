// lo que enviamos al backend para login/registro
export interface AuthRequest {
  username: string;
  password: string;
}

// lo que recibimos del backend después del login
export interface AuthResponse {
  token: string;
  role: string;
}