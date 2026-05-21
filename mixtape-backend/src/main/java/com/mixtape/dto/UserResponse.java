package com.mixtape.dto;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String email,
        String bio,
        String avatarUrl,
        LocalDateTime createdAt
) {}