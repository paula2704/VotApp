package com.VotApp.demo.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SurveyRequest {

    @NotBlank(message = "La pregunta es obligatoria")
    private String question;
    @NotEmpty(message = "Debe tener al menos una opción")
    @Size(min = 2, message = "La encuesta debe tener mínimo 2 opciones")
    private List<String> options; // lista de textos: ["Sí", "No", "Tal vez"]
}