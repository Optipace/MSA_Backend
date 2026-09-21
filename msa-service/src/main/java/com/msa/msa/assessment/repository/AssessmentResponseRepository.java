package com.msa.msa.assessment.repository;

import com.msa.msa.assessment.entity.AssessmentResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AssessmentResponseRepository
        extends JpaRepository<AssessmentResponse, UUID> {

    Optional<AssessmentResponse> findByAssessmentModuleInstanceId(
            UUID assessmentModuleInstanceId
    );
}