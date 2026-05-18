package com.mixtape.repository;

import com.mixtape.model.ShelfEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ShelfEntryRepository extends JpaRepository<ShelfEntry, Long> {
    List<ShelfEntry> findByUserIdOrderBySavedAtDesc(Long userId);
    boolean existsByUserIdAndMixtapeId(Long userId, Long mixtapeId);
    Optional<ShelfEntry> findByUserIdAndMixtapeId(Long userId, Long mixtapeId);
}