package com.lbgconnect.repository;

import com.lbgconnect.model.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @EntityGraph(attributePaths = {"reviewer"})
    List<Review> findByArtisanIdOrderByCreatedAtDesc(Long artisanId);
}
