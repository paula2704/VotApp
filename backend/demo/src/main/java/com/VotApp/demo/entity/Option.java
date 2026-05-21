package com.VotApp.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "options")
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @ManyToOne → muchas opciones pertenecen a una encuesta
    @ManyToOne
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey; // encuesta a la que pertenece esta opción

    @Column(nullable = false)
    private String text; // texto de la opción, ej: "Sí", "No", "Tal vez"
}