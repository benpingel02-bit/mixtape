package com.mixtape.dto;

public record AuthResponse(
        String token,
        String username,
        Long userId
) {}