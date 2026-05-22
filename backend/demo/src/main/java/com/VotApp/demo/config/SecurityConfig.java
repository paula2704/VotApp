package com.VotApp.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.VotApp.demo.security.JwtFilter;

import lombok.RequiredArgsConstructor;
// @Configuration le dice a Spring que esta clase define beans (componentes)
// Configura qué endpoints son públicos y cuáles requieren rol
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // desactiva CSRF porque usamos JWT, no sesiones
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())
            // STATELESS significa que Spring no guarda sesiones
            // cada petición debe traer su propio token
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                // endpoints públicos — no requieren token
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // solo ADMIN puede crear, editar y eliminar encuestas
                .requestMatchers(HttpMethod.POST, "/api/surveys").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/surveys/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/surveys/**").hasRole("ADMIN")

                // cualquier usuario autenticado puede ver encuestas y votar
                .anyRequest().authenticated()
            )

            // agrega nuestro filtro JWT antes del filtro de autenticación de Spring
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // BCrypt es el algoritmo estándar para encriptar contraseñas
    // nunca guardes contraseñas en texto plano
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager es necesario para el proceso de login
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}