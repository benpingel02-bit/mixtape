package com.mixtape.repository;

import com.mixtape.model.Mixtape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface MixtapeRepository extends JpaRepository<Mixtape, Long> {
    List<Mixtape> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Mixtape> findByIsPublicTrueOrderByCreatedAtDesc();

    // JOIN FETCH vermeidet N+1-Problem beim Laden der Tracks
    @Query("SELECT m FROM Mixtape m LEFT JOIN FETCH m.tracks WHERE m.id = :id")
    java.util.Optional<Mixtape> findByIdWithTracks(Long id);
}