package com.mixtape.service;

import com.mixtape.dto.TrackResponse;
import com.mixtape.dto.MixtapeRequest;
import com.mixtape.dto.MixtapeResponse;
import com.mixtape.exception.BusinessRuleException;
import com.mixtape.exception.ResourceNotFoundException;
import com.mixtape.model.Mixtape;
import com.mixtape.model.User;
import com.mixtape.repository.MixtapeRepository;
import com.mixtape.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MixtapeService {

    private final MixtapeRepository mixtapeRepository;
    private final UserRepository userRepository;

    public MixtapeService(MixtapeRepository mixtapeRepository,
                          UserRepository userRepository) {
        this.mixtapeRepository = mixtapeRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<MixtapeResponse> findAllPublic() {
        return mixtapeRepository.findByIsPublicTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MixtapeResponse findById(Long id) {
        Mixtape mixtape = mixtapeRepository.findByIdWithTracks(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mixtape not found: " + id));
        return toResponse(mixtape);
    }

    @Transactional(readOnly = true)
    public List<MixtapeResponse> findByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        return mixtapeRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public MixtapeResponse create(MixtapeRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.userId()));

        Mixtape mixtape = new Mixtape();
        mixtape.setTitle(request.title());
        mixtape.setDescription(request.description());
        mixtape.setCassetteType(request.cassetteType());
        mixtape.setDesignTemplate(request.designTemplate());
        mixtape.setLabelColor(request.labelColor());
        mixtape.setPublic(request.isPublic());
        mixtape.setUser(user);

        return toResponse(mixtapeRepository.save(mixtape));
    }

    public MixtapeResponse update(Long id, MixtapeRequest request) {
        Mixtape mixtape = mixtapeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mixtape not found: " + id));

        if (mixtape.isLocked()) {
            throw new BusinessRuleException("Mixtape is locked and cannot be modified");
        }

        mixtape.setTitle(request.title());
        mixtape.setDescription(request.description());
        mixtape.setDesignTemplate(request.designTemplate());
        mixtape.setLabelColor(request.labelColor());
        mixtape.setPublic(request.isPublic());
        // cassetteType ist nach Erstellung nicht mehr änderbar

        return toResponse(mixtapeRepository.save(mixtape));
    }

    public void delete(Long id) {
        if (!mixtapeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mixtape not found: " + id);
        }
        mixtapeRepository.deleteById(id);
    }

    public MixtapeResponse lock(Long id) {
        Mixtape mixtape = mixtapeRepository.findByIdWithTracks(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mixtape not found: " + id));

        if (mixtape.getTracks().isEmpty()) {
            throw new BusinessRuleException("Cannot lock a mixtape with no tracks");
        }

        mixtape.setLocked(true);
        return toResponse(mixtapeRepository.save(mixtape));
    }

    private MixtapeResponse toResponse(Mixtape m) {
        int totalDuration = m.getTracks().stream()
                .mapToInt(t -> t.getDurationSeconds())
                .sum();

        List<TrackResponse> tracks = m.getTracks().stream()
                .map(t -> new TrackResponse(
                        t.getId(),
                        t.getSpotifyTrackId(),
                        t.getTitle(),
                        t.getArtist(),
                        t.getAlbumName(),
                        t.getAlbumCoverUrl(),
                        t.getDurationSeconds(),
                        t.getPosition(),
                        m.getId()
                ))
                .toList();

        return new MixtapeResponse(
                m.getId(),
                m.getTitle(),
                m.getDescription(),
                m.getCassetteType(),
                m.getDesignTemplate(),
                m.getLabelColor(),
                m.isPublic(),
                m.isLocked(),
                m.getCreatedAt(),
                m.getUser().getId(),
                m.getUser().getUsername(),
                m.getTracks().size(),
                totalDuration,
                m.getCassetteType().getMaxDurationSeconds(),
                tracks
        );
    }
}