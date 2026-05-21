package com.VotApp.demo.entity;

import java.time.LocalDateTime;

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
@Table(name = "votes")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // relación con la encuesta en la que se votó
    @ManyToOne
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    // relación con la opción específica que eligió el usuario
    @ManyToOne
    @JoinColumn(name = "option_id", nullable = false)
    private Option option;

    // relación con el usuario que emitió el voto
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime votedAt = LocalDateTime.now(); // fecha y hora exacta del voto
}