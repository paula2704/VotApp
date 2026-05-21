package com.VotApp.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VoteRequest {

    @NotNull(message = "Debes seleccionar una opción")
    private Long optionId; // el usuario solo envía qué opción eligió
                           // el surveyId viene en la URL, no en el body
}