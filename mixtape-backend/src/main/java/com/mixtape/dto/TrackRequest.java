package com.mixtape.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrackRequest(
        @NotBlank(message = "Spotify track ID is required")
        String spotifyTrackId,

        @NotNull(message = "Mixtape ID is required")
        Long mixtapeId
) {}