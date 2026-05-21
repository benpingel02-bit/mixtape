package com.mixtape.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mixtape.controller.TrackController;
import com.mixtape.dto.TrackRequest;
import com.mixtape.exception.GlobalExceptionHandler;
import com.mixtape.exception.ResourceNotFoundException;
import com.mixtape.model.CassetteType;
import com.mixtape.model.Mixtape;
import com.mixtape.model.Track;
import com.mixtape.model.User;
import com.mixtape.service.TrackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TrackControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TrackService trackService;

    @InjectMocks
    private TrackController trackController;

    private Mixtape testMixtape;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(trackController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        User user = new User("testuser", "test@mixtape.de", "hash");
        testMixtape = new Mixtape();
        testMixtape.setTitle("Test Mixtape");
        testMixtape.setCassetteType(CassetteType.C90);
        testMixtape.setUser(user);
    }

    @Test
    void getTracksByMixtape_shouldReturn200WithList() throws Exception {
        Track track = new Track("spotify123", "Song 1", "Artist", "Album", null, 200, 1, testMixtape);
        when(trackService.findByMixtapeId(1L)).thenReturn(List.of(track));

        mockMvc.perform(get("/api/mixtapes/1/tracks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Song 1"))
                .andExpect(jsonPath("$[0].artist").value("Artist"));
    }

    @Test
    void getTracksByMixtape_shouldReturn404_whenMixtapeNotFound() throws Exception {
        when(trackService.findByMixtapeId(99L))
                .thenThrow(new ResourceNotFoundException("Mixtape not found: 99"));

        mockMvc.perform(get("/api/mixtapes/99/tracks"))
                .andExpect(status().isNotFound());
    }
}