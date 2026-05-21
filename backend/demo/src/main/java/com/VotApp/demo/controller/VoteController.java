package com.VotApp.demo.controller;

import com.VotApp.demo.dto.VoteRequest;
import com.VotApp.demo.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/surveys")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    // POST /api/surveys/{id}/vote — votar en una encuesta
    // el surveyId viene en la URL, la opción elegida viene en el body
    @PostMapping("/{id}/vote")
    public ResponseEntity<Void> vote(
            @PathVariable Long id,
            @Valid @RequestBody VoteRequest request,
            Authentication authentication) {
        voteService.vote(id, request, authentication.getName());
        return ResponseEntity.ok().build();
    }
}