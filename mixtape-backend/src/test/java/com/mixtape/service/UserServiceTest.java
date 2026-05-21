package com.mixtape.service;

import com.mixtape.dto.UserRequest;
import com.mixtape.dto.UserResponse;
import com.mixtape.exception.BusinessRuleException;
import com.mixtape.exception.ResourceNotFoundException;
import com.mixtape.model.User;
import com.mixtape.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "test@mixtape.de", "hashedpassword");
    }

    @Test
    void findById_shouldReturnUserResponse_whenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponse response = userService.findById(1L);

        assertThat(response.username()).isEqualTo("testuser");
        assertThat(response.email()).isEqualTo("test@mixtape.de");
    }

    @Test
    void findById_shouldThrowResourceNotFoundException_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void create_shouldThrowBusinessRuleException_whenUsernameAlreadyTaken() {
        UserRequest request = new UserRequest("testuser", "new@mixtape.de", "password123", null, null);

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Username already taken");

        verify(userRepository, never()).save(any());
    }
}