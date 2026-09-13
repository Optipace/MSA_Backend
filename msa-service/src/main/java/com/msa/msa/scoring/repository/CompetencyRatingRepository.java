package com.msa.msa.scoring.repository;

import com.msa.msa.scoring.entity.CompetencyRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompetencyRatingRepository
        extends JpaRepository<CompetencyRating, Long> {

    Optional<CompetencyRating> findByRatingCode(String ratingCode);
}