package com.mixtape.service;

import com.mixtape.dto.UserRequest;
import com.mixtape.dto.UserResponse;
import com.mixtape.exception.BusinessRuleException;
import com.mixtape.exception.ResourceNotFoundException;
import com.mixtape.model.User;
import com.mixtape.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return toResponse(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id)));
    }

    @Transactional(readOnly = true)
    public UserResponse findByUsername(String username) {
        return toResponse(userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username)));
    }

    public UserResponse create(UserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessRuleException("Username already taken: " + request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Email already in use: " + request.email());
        }

        // Passwort wird hier als Klartext gespeichert – wird mit JWT durch BCrypt ersetzt
        User user = new User(request.username(), request.email(), request.password());
        user.setBio(request.bio());
        user.setAvatarUrl(request.avatarUrl());

        return toResponse(userRepository.save(user));
    }

    public UserResponse update(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        if (!user.getUsername().equals(request.username())
                && userRepository.existsByUsername(request.username())) {
            throw new BusinessRuleException("Username already taken: " + request.username());
        }

        user.setUsername(request.username());
        user.setBio(request.bio());
        user.setAvatarUrl(request.avatarUrl());

        return toResponse(userRepository.save(user));
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getBio(),
                user.getAvatarUrl(),
                user.getCreatedAt()
        );
    }
}