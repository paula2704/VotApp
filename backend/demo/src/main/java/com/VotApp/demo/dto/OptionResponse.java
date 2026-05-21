package com.VotApp.demo.dto;

import lombok.Data;

@Data
public class OptionResponse {
    private Long id;
    private String text;    // texto de la opción
    private Long votes;     // cuántos votos tiene esta opción
}