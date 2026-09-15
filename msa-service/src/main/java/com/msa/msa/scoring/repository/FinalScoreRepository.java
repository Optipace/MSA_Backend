package com.msa.msa.scoring.repository;

import com.msa.msa.scoring.entity.FinalScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FinalScoreRepository
        extends JpaRepository<FinalScore, UUID> {

    Optional<FinalScore> findByAssessmentSessionId(
            UUID assessmentSessionId
    );
}