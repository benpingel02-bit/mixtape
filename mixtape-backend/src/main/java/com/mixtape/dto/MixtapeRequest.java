package com.mixtape.dto;

import com.mixtape.model.CassetteType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MixtapeRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title must not exceed 100 characters")
        String title,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @NotNull(message = "Cassette type is required")
        CassetteType cassetteType,

        String designTemplate,
        String labelColor,
        boolean isPublic,

        // wird später durch JWT-Principal ersetzt
        @NotNull(message = "User ID is required")
        Long userId
) {}