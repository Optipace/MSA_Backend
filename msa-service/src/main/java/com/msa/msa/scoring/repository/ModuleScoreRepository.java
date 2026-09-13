package com.msa.msa.scoring.repository;

import com.msa.msa.scoring.entity.ModuleScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ModuleScoreRepository
        extends JpaRepository<ModuleScore, UUID> {

    Optional<ModuleScore> findByAssessmentModuleInstanceId(
            UUID assessmentModuleInstanceId
    );
}