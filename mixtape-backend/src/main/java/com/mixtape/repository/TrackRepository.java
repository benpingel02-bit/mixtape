package com.mixtape.repository;

import com.mixtape.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TrackRepository extends JpaRepository<Track, Long> {
    List<Track> findByMixtapeIdOrderByPosition(Long mixtapeId);
    void deleteByMixtapeId(Long mixtapeId);
}