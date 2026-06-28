package com.mixtape.controller;

import com.mixtape.model.Mixtape;
import com.mixtape.repository.MixtapeRepository;
import com.mixtape.repository.TrackRepository;
import com.mixtape.service.SpotifyOAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/spotify")
public class SpotifyOAuthController {

    private final SpotifyOAuthService spotifyOAuthService;
    private final MixtapeRepository mixtapeRepository;
    private final TrackRepository trackRepository;

    public SpotifyOAuthController(
            SpotifyOAuthService spotifyOAuthService,
            MixtapeRepository mixtapeRepository,
            TrackRepository trackRepository
    ) {
        this.spotifyOAuthService = spotifyOAuthService;
        this.mixtapeRepository = mixtapeRepository;
        this.trackRepository = trackRepository;
    }

    // Schritt 1: Auth-URL für Spotify-Login generieren
    @GetMapping("/auth-url")
    public ResponseEntity<Map<String, String>> getAuthUrl(@RequestParam Long userId) {
        String url = spotifyOAuthService.buildAuthorizationUrl(userId);
        return ResponseEntity.ok(Map.of("url", url));
    }

    // Schritt 2: Spotify leitet hierher zurück mit Code
    @GetMapping("/callback")
    public ResponseEntity<Void> callback(
            @RequestParam String code,
            @RequestParam String state
    ) {
        Long userId = Long.parseLong(state);
        spotifyOAuthService.exchangeCodeForToken(code, userId);

        // Weiterleitung zum Frontend
        return ResponseEntity.status(302)
                .header("Location", "http://localhost:5173/spotify-connected")
                .build();
    }

    // Schritt 3: Playlist aus Mixtape erstellen
    @PostMapping("/export/{mixtapeId}")
    public ResponseEntity<Map<String, String>> exportToSpotify(
            @PathVariable Long mixtapeId,
            @RequestParam Long userId
    ) {
        Mixtape mixtape = mixtapeRepository.findById(mixtapeId)
                .orElseThrow(() -> new RuntimeException("Mixtape not found: " + mixtapeId));

        List<String> trackIds = trackRepository.findByMixtapeIdOrderByPosition(mixtapeId)
                .stream()
                .map(t -> t.getSpotifyTrackId())
                .toList();

        String playlistUrl = spotifyOAuthService.createPlaylist(userId, mixtape.getTitle(), trackIds);

        return ResponseEntity.ok(Map.of("playlistUrl", playlistUrl));
    }

    // Prüfen ob User bereits mit Spotify verbunden ist
    @GetMapping("/connected")
    public ResponseEntity<Map<String, Boolean>> isConnected(@RequestParam Long userId) {
        return ResponseEntity.ok(Map.of("connected", spotifyOAuthService.hasToken(userId)));
    }
}