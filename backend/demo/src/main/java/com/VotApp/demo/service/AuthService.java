package com.VotApp.demo.service;

import com.VotApp.demo.dto.AuthRequest;
import com.VotApp.demo.dto.AuthResponse;
import com.VotApp.demo.entity.User;
import com.VotApp.demo.repository.UserRepository;
import com.VotApp.demo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(AuthRequest request) {
        // busca el usuario en la BD
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // verifica que la contraseña coincida con la encriptada en la BD
        // BCrypt compara el texto plano con el hash, nunca desencripta
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // genera el token con username y rol
        String token = jwtService.generateToken(
            user.getUsername(),
            user.getRole().name()
        );

        return new AuthResponse(token, user.getRole().name());
    }

    public AuthResponse register(AuthRequest request, String role) {
        // verifica que el username no exista
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        // crea el usuario nuevo
        User user = new User();
        user.setUsername(request.getUsername());
        // encripta la contraseña antes de guardarla
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(com.VotApp.demo.entity.Role.valueOf(role));

        userRepository.save(user);

        String token = jwtService.generateToken(
            user.getUsername(),
            user.getRole().name()
        );

        return new AuthResponse(token, user.getRole().name());
    }
}