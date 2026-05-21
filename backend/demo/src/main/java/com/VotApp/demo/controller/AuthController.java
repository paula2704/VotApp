package com.VotApp.demo.controller;

import com.VotApp.demo.dto.AuthRequest;
import com.VotApp.demo.dto.AuthResponse;
import com.VotApp.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// @RestController combina @Controller + @ResponseBody
// significa que todos los métodos retornan JSON automáticamente
// @RequestMapping define el prefijo de todos los endpoints de este controller
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/login
    // @Valid activa las validaciones del DTO (los @NotBlank, etc.)
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // POST /api/auth/register/user
    @PostMapping("/register/user")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.register(request, "USER"));
    }

    // POST /api/auth/register/admin
    @PostMapping("/register/admin")
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.register(request, "ADMIN"));
    }
}