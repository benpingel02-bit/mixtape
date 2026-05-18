package com.mixtape.repository;

import com.mixtape.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndMixtapeId(Long userId, Long mixtapeId);
    long countByMixtapeId(Long mixtapeId);
    Optional<Like> findByUserIdAndMixtapeId(Long userId, Long mixtapeId);
}