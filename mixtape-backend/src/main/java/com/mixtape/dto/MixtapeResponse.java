package com.mixtape.dto;

import com.mixtape.model.CassetteType;
import java.time.LocalDateTime;

public record MixtapeResponse(
        Long id,
        String title,
        String description,
        CassetteType cassetteType,
        String designTemplate,
        String labelColor,
        boolean isPublic,
        boolean isLocked,
        LocalDateTime createdAt,
        Long userId,
        String username,
        int trackCount,
        int totalDurationSeconds,
        int maxDurationSeconds
) {}