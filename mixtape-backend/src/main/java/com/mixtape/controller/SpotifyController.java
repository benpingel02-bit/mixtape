package com.mixtape.controller;

import com.mixtape.client.SpotifyApiClient;
import com.mixtape.dto.SpotifyTrackResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/spotify")
public class SpotifyController {

    private final SpotifyApiClient spotifyApiClient;

    public SpotifyController(SpotifyApiClient spotifyApiClient) {
        this.spotifyApiClient = spotifyApiClient;
    }

    @GetMapping("/search")
    public List<SpotifyTrackResponse> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit) {

        SpotifyApiClient.SpotifySearchResult result = spotifyApiClient.search(q, limit);

        if (result == null || result.tracks() == null) {
            return List.of();
        }

        return result.tracks().items().stream()
                .map(t -> new SpotifyTrackResponse(
                        t.id(),
                        t.name(),
                        t.primaryArtist(),
                        t.album().name(),
                        t.coverUrl(),
                        t.durationSeconds()
                ))
                .toList();
    }
}