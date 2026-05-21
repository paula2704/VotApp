package com.VotApp.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SurveyResponse {
    private Long id;
    private String question;
    private String createdBy;    // solo el username, no el objeto User completo
    private Boolean active;
    private LocalDateTime createdAt;
    private List<OptionResponse> options; // lista de opciones con sus votos
}