package com.mixtape.service;

import com.mixtape.client.SpotifyApiClient;
import com.mixtape.dto.TrackRequest;
import com.mixtape.exception.BusinessRuleException;
import com.mixtape.exception.ResourceNotFoundException;
import com.mixtape.model.CassetteType;
import com.mixtape.model.Mixtape;
import com.mixtape.model.Track;
import com.mixtape.model.User;
import com.mixtape.repository.MixtapeRepository;
import com.mixtape.repository.TrackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackServiceTest {

    @Mock
    private TrackRepository trackRepository;

    @Mock
    private MixtapeRepository mixtapeRepository;

    @Mock
    private SpotifyApiClient spotifyApiClient;

    @InjectMocks
    private TrackService trackService;

    private Mixtape testMixtape;

    @BeforeEach
    void setUp() {
        User user = new User("testuser", "test@mixtape.de", "hash");

        testMixtape = new Mixtape();
        testMixtape.setTitle("Test Mixtape");
        testMixtape.setCassetteType(CassetteType.C90);
        testMixtape.setLocked(false);
        testMixtape.setUser(user);
    }

    @Test
    void addTrack_shouldThrowBusinessRuleException_whenMixtapeLocked() {
        testMixtape.setLocked(true);
        when(mixtapeRepository.findByIdWithTracks(1L)).thenReturn(Optional.of(testMixtape));

        TrackRequest request = new TrackRequest("spotify123", 1L);

        assertThatThrownBy(() -> trackService.addTrack(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("locked");
    }

    @Test
    void addTrack_shouldThrowBusinessRuleException_whenCassetteFull() {
        // Nur durationSeconds wird vor der Exception geprüft
        SpotifyApiClient.SpotifyTrack spotifyTrack = mock(SpotifyApiClient.SpotifyTrack.class);
        when(spotifyTrack.durationSeconds()).thenReturn(5401);

        when(mixtapeRepository.findByIdWithTracks(1L)).thenReturn(Optional.of(testMixtape));
        when(spotifyApiClient.getTrack("spotify123")).thenReturn(spotifyTrack);

        TrackRequest request = new TrackRequest("spotify123", 1L);

        assertThatThrownBy(() -> trackService.addTrack(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("cassette limit");
    }

    @Test
    void deleteTrack_shouldRenumberPositions_afterDeletion() {
        Track track1 = new Track("sp1", "Song 1", "Artist", "Album", null, 200, 1, testMixtape);
        Track track2 = new Track("sp2", "Song 2", "Artist", "Album", null, 200, 2, testMixtape);
        Track track3 = new Track("sp3", "Song 3", "Artist", "Album", null, 200, 3, testMixtape);

        when(trackRepository.findById(1L)).thenReturn(Optional.of(track1));
        when(trackRepository.findByMixtapeIdOrderByPosition(any()))
                .thenReturn(new ArrayList<>(List.of(track2, track3)));

        trackService.deleteTrack(1L);

        // Nach Renumbering: track2 = Position 1, track3 = Position 2
        assertThat(track2.getPosition()).isEqualTo(1);
        assertThat(track3.getPosition()).isEqualTo(2);
        verify(trackRepository).saveAll(any());
    }
}