package com.mixtape.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SpotifyOAuthService {

    @Value("${spotify.client-id}")
    private String clientId;

    @Value("${spotify.client-secret}")
    private String clientSecret;

    @Value("${spotify.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

    private final ConcurrentHashMap<Long, String> userTokens = new ConcurrentHashMap<>();

    public String buildAuthorizationUrl(Long userId) {
        return "https://accounts.spotify.com/authorize" +
                "?client_id=" + clientId +
                "&response_type=code" +
                "&redirect_uri=" + redirectUri +
                "&scope=playlist-modify-public playlist-modify-private user-read-private" +
                "&state=" + userId;
    }

    public void exchangeCodeForToken(String code, Long userId) {
        String credentials = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + credentials);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://accounts.spotify.com/api/token", request, Map.class);

        String accessToken = (String) response.getBody().get("access_token");
        System.out.println("Spotify Token erhalten für User " + userId + ": " + accessToken);
        userTokens.put(userId, accessToken);
    }

    public String createPlaylist(Long userId, String mixtapeTitle, List<String> spotifyTrackIds) {
        String token = userTokens.get(userId);
        if (token == null) {
            throw new IllegalStateException("No Spotify token for user: " + userId);
        }

        try {
            // Profil holen
            HttpHeaders profileHeaders = new HttpHeaders();
            profileHeaders.set("Authorization", "Bearer " + token);
            HttpEntity<?> profileEntity = new HttpEntity<>(profileHeaders);

            ResponseEntity<Map> profileResponse = restTemplate.exchange(
                    "https://api.spotify.com/v1/me", HttpMethod.GET, profileEntity, Map.class);
            String spotifyUserId = (String) profileResponse.getBody().get("id");
            System.out.println("Spotify User ID: " + spotifyUserId);
            System.out.println("Komplette Profile Response: " + profileResponse.getBody());

            // Playlist erstellen
            HttpHeaders playlistHeaders = new HttpHeaders();
            playlistHeaders.set("Authorization", "Bearer " + token);
            playlistHeaders.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> playlistBody = Map.of(
                    "name", mixtapeTitle,
                    "description", "Erstellt mit MixTape",
                    "public", false
            );

            HttpEntity<Map<String, Object>> createRequest = new HttpEntity<>(playlistBody, playlistHeaders);
            ResponseEntity<Map> playlistResponse = restTemplate.postForEntity(
                    "https://api.spotify.com/v1/me/playlists",
                    createRequest, Map.class);

            String playlistId = (String) playlistResponse.getBody().get("id");
            System.out.println("Playlist ID: " + playlistId);

            // Tracks hinzufügen
            HttpHeaders tracksHeaders = new HttpHeaders();
            tracksHeaders.set("Authorization", "Bearer " + token);
            tracksHeaders.setContentType(MediaType.APPLICATION_JSON);

            List<String> uris = spotifyTrackIds.stream()
                    .map(id -> "spotify:track:" + id)
                    .toList();

            Map<String, Object> tracksBody = Map.of("uris", uris);
            HttpEntity<Map<String, Object>> tracksRequest = new HttpEntity<>(tracksBody, tracksHeaders);

            restTemplate.postForEntity(
                    "https://api.spotify.com/v1/playlists/" + playlistId + "/tracks",
                    tracksRequest, Map.class);

            return "https://open.spotify.com/playlist/" + playlistId;

        } catch (Exception e) {
            System.err.println("Spotify Export Fehler: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public boolean hasToken(Long userId) {
        return userTokens.containsKey(userId);
    }
}