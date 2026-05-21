package com.VotApp.demo.controller;

import com.VotApp.demo.dto.SurveyRequest;
import com.VotApp.demo.dto.SurveyResponse;
import com.VotApp.demo.service.SurveyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/surveys")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    // GET /api/surveys — trae todas las encuestas (admin ve todas)
    @GetMapping
    public ResponseEntity<List<SurveyResponse>> getAll() {
        return ResponseEntity.ok(surveyService.getAll());
    }

    // GET /api/surveys/active — solo las encuestas activas (para usuarios)
    @GetMapping("/active")
    public ResponseEntity<List<SurveyResponse>> getActive() {
        return ResponseEntity.ok(surveyService.getActive());
    }

    // GET /api/surveys/{id} — una encuesta específica
    @GetMapping("/{id}")
    public ResponseEntity<SurveyResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(surveyService.getById(id));
    }

    // POST /api/surveys — crear encuesta (solo ADMIN)
    // Authentication viene de Spring Security, tiene el username del token
    @PostMapping
    public ResponseEntity<SurveyResponse> create(
            @Valid @RequestBody SurveyRequest request,
            Authentication authentication) {
        // authentication.getName() retorna el username del token JWT
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(surveyService.create(request, authentication.getName()));
    }

    // PUT /api/surveys/{id} — editar encuesta (solo ADMIN)
    @PutMapping("/{id}")
    public ResponseEntity<SurveyResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SurveyRequest request) {
        return ResponseEntity.ok(surveyService.update(id, request));
    }

    // DELETE /api/surveys/{id} — eliminar encuesta (solo ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        surveyService.delete(id);
        // 204 No Content — éxito pero sin cuerpo en la respuesta
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/surveys/{id}/toggle — activar/desactivar encuesta (solo ADMIN)
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Void> toggle(@PathVariable Long id) {
        surveyService.toggleActive(id);
        return ResponseEntity.noContent().build();
    }
}