package com.mixtape.dto;

public record TrackResponse(
        Long id,
        String spotifyTrackId,
        String title,
        String artist,
        String albumName,
        String albumCoverUrl,
        int durationSeconds,
        int position,
        Long mixtapeId
) {}