package com.mixtape.dto;

public record SpotifyTrackResponse(
        String spotifyTrackId,
        String title,
        String artist,
        String albumName,
        String albumCoverUrl,
        int durationSeconds
) {}