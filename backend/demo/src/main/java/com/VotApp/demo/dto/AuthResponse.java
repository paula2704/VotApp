package com.VotApp.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token; // el JWT que el frontend debe guardar y enviar en cada petición
    private String role;  // "ADMIN" o "USER" para que el frontend sepa qué vistas mostrar
}