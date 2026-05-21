package com.mixtape.service;

import com.mixtape.dto.MixtapeRequest;
import com.mixtape.dto.MixtapeResponse;
import com.mixtape.exception.BusinessRuleException;
import com.mixtape.exception.ResourceNotFoundException;
import com.mixtape.model.CassetteType;
import com.mixtape.model.Mixtape;
import com.mixtape.model.User;
import com.mixtape.repository.MixtapeRepository;
import com.mixtape.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MixtapeServiceTest {

    @Mock
    private MixtapeRepository mixtapeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MixtapeService mixtapeService;

    private User testUser;
    private Mixtape testMixtape;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "test@mixtape.de", "hash");

        testMixtape = new Mixtape();
        testMixtape.setTitle("Test Mixtape");
        testMixtape.setCassetteType(CassetteType.C90);
        testMixtape.setPublic(true);
        testMixtape.setUser(testUser);
    }

    @Test
    void create_shouldReturnMixtapeResponse_whenUserExists() {
        MixtapeRequest request = new MixtapeRequest(
                "Test Mixtape", "Beschreibung", CassetteType.C90,
                null, "#e94560", true, 1L
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(mixtapeRepository.save(any(Mixtape.class))).thenReturn(testMixtape);

        MixtapeResponse response = mixtapeService.create(request);

        assertThat(response.title()).isEqualTo("Test Mixtape");
        assertThat(response.cassetteType()).isEqualTo(CassetteType.C90);
        verify(mixtapeRepository).save(any(Mixtape.class));
    }

    @Test
    void create_shouldThrowResourceNotFoundException_whenUserNotFound() {
        MixtapeRequest request = new MixtapeRequest(
                "Test Mixtape", null, CassetteType.C60,
                null, null, true, 99L
        );

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mixtapeService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void lock_shouldThrowBusinessRuleException_whenNoTracks() {
        testMixtape.setLocked(false);

        when(mixtapeRepository.findByIdWithTracks(1L)).thenReturn(Optional.of(testMixtape));

        assertThatThrownBy(() -> mixtapeService.lock(1L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("no tracks");
    }

    @Test
    void findById_shouldThrowResourceNotFoundException_whenNotFound() {
        when(mixtapeRepository.findByIdWithTracks(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mixtapeService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Mixtape not found");
    }
}