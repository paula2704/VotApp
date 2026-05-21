package com.VotApp.demo.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

// OncePerRequestFilter garantiza que este filtro se ejecuta exactamente una vez por petición
// @RequiredArgsConstructor genera constructor con los campos final (inyección de dependencias)
// Este filtro se encarga de leer el token JWT de cada petición, validarlo y autenticar al usuario
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // lee el header Authorization de la petición
        String authHeader = request.getHeader("Authorization");

        // si no hay header o no empieza con "Bearer ", deja pasar la petición sin autenticar
        // Spring Security después decide si el endpoint requiere autenticación o no
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // extrae el token quitando el prefijo "Bearer "
        String token = authHeader.substring(7);

        // valida el token y autentica al usuario
        if (jwtService.isTokenValid(token)) {
            String username = jwtService.getUsername(token);
            String role = jwtService.getRole(token);

            // crea el objeto de autenticación con el rol del usuario
            // "ROLE_" es el prefijo que Spring Security espera
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

            // guarda la autenticación en el contexto de Spring Security
            // a partir de aquí Spring sabe quién es el usuario en esta petición
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // continúa con el siguiente filtro en la cadena
        filterChain.doFilter(request, response);
    }
}