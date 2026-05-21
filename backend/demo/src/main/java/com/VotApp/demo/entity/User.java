package com.VotApp.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

// @Entity le dice a JPA que esta clase representa una tabla en la BD
// @Table define el nombre exacto de la tabla en PostgreSQL
// @Data de Lombok genera automáticamente getters, setters, toString, equals y hashCode
@Data
@Entity
@Table(name = "users")
public class User {

    // @Id indica que este campo es la clave primaria
    // @GeneratedValue hace que PostgreSQL genere el ID automáticamente (autoincremental)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column define restricciones de la columna en la BD
    // nullable = false → NOT NULL | unique = true → no puede repetirse
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password; // se guardará encriptada con BCrypt, nunca en texto plano

    // @Enumerated(STRING) guarda el rol como texto ("ADMIN" o "USER") en la BD
    // sin esto lo guardaría como número (0 o 1), que es menos legible
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}