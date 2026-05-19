package com.mixtape.service;

import com.mixtape.client.SpotifyApiClient;
import com.mixtape.dto.TrackRequest;
import com.mixtape.dto.TrackResponse;
import com.mixtape.exception.BusinessRuleException;
import com.mixtape.exception.ResourceNotFoundException;
import com.mixtape.model.Mixtape;
import com.mixtape.model.Track;
import com.mixtape.repository.MixtapeRepository;
import com.mixtape.repository.TrackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TrackService {

    private final TrackRepository trackRepository;
    private final MixtapeRepository mixtapeRepository;
    private final SpotifyApiClient spotifyApiClient;

    public TrackService(TrackRepository trackRepository,
                        MixtapeRepository mixtapeRepository,
                        SpotifyApiClient spotifyApiClient) {
        this.trackRepository = trackRepository;
        this.mixtapeRepository = mixtapeRepository;
        this.spotifyApiClient = spotifyApiClient;
    }

    @Transactional(readOnly = true)
    public List<TrackResponse> findByMixtape(Long mixtapeId) {
        if (!mixtapeRepository.existsById(mixtapeId)) {
            throw new ResourceNotFoundException("Mixtape not found: " + mixtapeId);
        }
        return trackRepository.findByMixtapeIdOrderByPosition(mixtapeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TrackResponse addTrack(TrackRequest request) {
        Mixtape mixtape = mixtapeRepository.findByIdWithTracks(request.mixtapeId())
                .orElseThrow(() -> new ResourceNotFoundException("Mixtape not found: " + request.mixtapeId()));

        if (mixtape.isLocked()) {
            throw new BusinessRuleException("Mixtape is locked, no tracks can be added");
        }

        // Spotify-Metadaten holen
        SpotifyApiClient.SpotifyTrack spotifyTrack = spotifyApiClient.getTrack(request.spotifyTrackId());
        if (spotifyTrack == null) {
            throw new ResourceNotFoundException("Spotify track not found: " + request.spotifyTrackId());
        }

        // Kassettenlänge prüfen
        int currentDuration = mixtape.getTracks().stream()
                .mapToInt(Track::getDurationSeconds)
                .sum();
        int newDuration = currentDuration + spotifyTrack.durationSeconds();
        if (newDuration > mixtape.getCassetteType().getMaxDurationSeconds()) {
            throw new BusinessRuleException(
                    "Track would exceed cassette limit of "
                            + mixtape.getCassetteType().getMaxDurationSeconds() + "s"
            );
        }

        // Position = nächste freie Stelle
        int nextPosition = mixtape.getTracks().size() + 1;

        Track track = new Track(
                spotifyTrack.id(),
                spotifyTrack.name(),
                spotifyTrack.primaryArtist(),
                spotifyTrack.album().name(),
                spotifyTrack.coverUrl(),
                spotifyTrack.durationSeconds(),
                nextPosition,
                mixtape
        );

        return toResponse(trackRepository.save(track));
    }

    public void deleteTrack(Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ResourceNotFoundException("Track not found: " + trackId));

        if (track.getMixtape().isLocked()) {
            throw new BusinessRuleException("Mixtape is locked, tracks cannot be removed");
        }

        Long mixtapeId = track.getMixtape().getId();
        trackRepository.deleteById(trackId);

        // Positionen lückenlos neu nummerieren
        List<Track> remaining = trackRepository.findByMixtapeIdOrderByPosition(mixtapeId);
        for (int i = 0; i < remaining.size(); i++) {
            remaining.get(i).setPosition(i + 1);
        }
        trackRepository.saveAll(remaining);
    }

    private TrackResponse toResponse(Track t) {
        return new TrackResponse(
                t.getId(),
                t.getSpotifyTrackId(),
                t.getTitle(),
                t.getArtist(),
                t.getAlbumName(),
                t.getAlbumCoverUrl(),
                t.getDurationSeconds(),
                t.getPosition(),
                t.getMixtape().getId()
        );
    }
}