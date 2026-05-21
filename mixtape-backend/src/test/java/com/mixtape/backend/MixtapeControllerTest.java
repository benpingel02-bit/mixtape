package com.mixtape.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mixtape.controller.MixtapeController;
import com.mixtape.dto.MixtapeRequest;
import com.mixtape.dto.MixtapeResponse;
import com.mixtape.exception.GlobalExceptionHandler;
import com.mixtape.exception.ResourceNotFoundException;
import com.mixtape.model.CassetteType;
import com.mixtape.service.MixtapeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MixtapeControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private MixtapeService mixtapeService;

    @InjectMocks
    private MixtapeController mixtapeController;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(mixtapeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    private MixtapeResponse sampleResponse() {
        return new MixtapeResponse(
                1L, "Test Mixtape", "Beschreibung", CassetteType.C90,
                null, "#e94560", true, false,
                LocalDateTime.now(), 1L, "testuser",
                0, 0, 5400, List.of()
        );
    }

    @Test
    void getAllPublic_shouldReturn200WithList() throws Exception {
        when(mixtapeService.findAllPublic()).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/mixtapes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Mixtape"))
                .andExpect(jsonPath("$[0].cassetteType").value("C90"));
    }

    @Test
    void getById_shouldReturn404_whenNotFound() throws Exception {
        when(mixtapeService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Mixtape not found: 99"));

        mockMvc.perform(get("/api/mixtapes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldReturn400_whenTitleMissing() throws Exception {
        MixtapeRequest invalidRequest = new MixtapeRequest(
                "", null, CassetteType.C90,
                null, null, true, 1L
        );

        mockMvc.perform(post("/api/mixtapes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}