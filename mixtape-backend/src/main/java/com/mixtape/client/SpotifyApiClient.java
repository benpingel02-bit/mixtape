package com.mixtape.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;

@Component
public class SpotifyApiClient {

    private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";
    private static final String API_BASE  = "https://api.spotify.com/v1";

    @Value("${spotify.client-id}")
    private String clientId;

    @Value("${spotify.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    private String cachedToken;
    private Instant tokenExpiresAt = Instant.EPOCH;

    // --- Token ---

    private String getAccessToken() {
        if (cachedToken != null && Instant.now().isBefore(tokenExpiresAt)) {
            return cachedToken;
        }
        return fetchNewToken();
    }

    private String fetchNewToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        TokenResponse response = restTemplate.postForObject(TOKEN_URL, request, TokenResponse.class);

        if (response == null) {
            throw new RuntimeException("Failed to obtain Spotify access token");
        }

        // 60 Sekunden Puffer vor Ablauf
        cachedToken = response.accessToken();
        tokenExpiresAt = Instant.now().plusSeconds(response.expiresIn() - 60);
        return cachedToken;
    }

    // --- Search ---

    public SpotifySearchResult search(String query, int limit) {
        String url = API_BASE + "/search?q=" + encode(query) + "&type=track&limit=" + limit;
        return restTemplate.exchange(url, HttpMethod.GET, authHeader(), SpotifySearchResult.class).getBody();
    }

    // --- Track by ID ---

    public SpotifyTrack getTrack(String spotifyTrackId) {
        String url = API_BASE + "/tracks/" + spotifyTrackId;
        return restTemplate.exchange(url, HttpMethod.GET, authHeader(), SpotifyTrack.class).getBody();
    }

    // --- Hilfsmethoden ---

    private HttpEntity<Void> authHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken());
        return new HttpEntity<>(headers);
    }

    private String encode(String value) {
        return java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8);
    }

    // --- DTOs für die Spotify-API-Antwort ---

    public record TokenResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("expires_in")  int expiresIn
    ) {}

    public record SpotifySearchResult(
            Tracks tracks
    ) {
        public record Tracks(List<SpotifyTrack> items) {}
    }

    public record SpotifyTrack(
            String id,
            String name,
            List<Artist> artists,
            @JsonProperty("album") Album album,
            @JsonProperty("duration_ms") int durationMs
    ) {
        public record Artist(String name) {}
        public record Album(
                String name,
                @JsonProperty("images") List<AlbumImage> images
        ) {}
        public record AlbumImage(String url, int height, int width) {}

        public String primaryArtist() {
            return artists.isEmpty() ? "Unknown" : artists.get(0).name();
        }

        public String coverUrl() {
            if (album == null || album.images().isEmpty()) return null;
            return album.images().get(0).url();
        }

        public int durationSeconds() {
            return durationMs / 1000;
        }
    }
}