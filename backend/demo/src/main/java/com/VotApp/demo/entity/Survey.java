package com.VotApp.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "surveys")
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question; // la pregunta de la encuesta

    // @ManyToOne → muchas encuestas pueden pertenecer a un usuario
    // @JoinColumn define la columna de clave foránea en la tabla surveys
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy; // el admin que creó esta encuesta

    @Column(nullable = false)
    private Boolean active = true; // si es false, los usuarios no pueden votar

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // se asigna automáticamente al crear

    // @OneToMany → una encuesta tiene muchas opciones
    // mappedBy = "survey" le dice a JPA que la relación ya está mapeada en Option
    // cascade ALL → si eliminas la encuesta, se eliminan sus opciones también
    // orphanRemoval → si quitas una opción de la lista, se borra de la BD
    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options;
}