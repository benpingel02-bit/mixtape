package com.mixtape.controller;

import com.mixtape.dto.AuthResponse;
import com.mixtape.dto.LoginRequest;
import com.mixtape.dto.RegisterRequest;
import com.mixtape.model.User;
import com.mixtape.repository.UserRepository;
import com.mixtape.security.JwtUtil;
import com.mixtape.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserService userService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UserRepository userRepository,
            UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        User user = userRepository.findByUsername(request.username()).orElseThrow();
        String token = jwtUtil.generateToken(user.getUsername());

        return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getId()));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            return ResponseEntity.badRequest().build();
        }

        User user = new User(request.username(), request.email(),
                userService.encodePassword(request.password()));
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getId()));
    }
}