package com.VotApp.demo.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

// Servicio para generar y validar tokens JWT
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    // convierte el secret string en una clave criptográfica real
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // genera un token JWT con el username y rol del usuario
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username)            // quien es el usuario
                .claim("role", role)          // dato extra que guardamos en el token
                .issuedAt(new Date())         // cuando se generó
                .expiration(new Date(System.currentTimeMillis() + expiration)) // cuando expira
                .signWith(getKey())           // firma con nuestra clave secreta
                .compact();                   // construye el string final
    }

    // extrae todos los datos (claims) del token
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())         // verifica que la firma sea válida
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // extrae el username del token
    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    // extrae el rol del token
    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // verifica si el token no ha expirado
    public boolean isTokenValid(String token) {
        try {
            return getClaims(token).getExpiration().after(new Date());
        } catch (Exception e) {
            // si el token está malformado o la firma no coincide, retorna false
            return false;
        }
    }
}