package com.msa.msa.assessment.repository;

import com.msa.msa.assessment.entity.AssessmentAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AssessmentAttemptRepository
        extends JpaRepository<AssessmentAttempt, UUID> {

    Optional<AssessmentAttempt>
    findFirstByAssessmentSessionAssessmentSessionIdOrderByAttemptNoAsc(
            UUID assessmentSessionId);
    Optional<AssessmentAttempt>
    findFirstByAssessmentSessionAssessmentSessionIdOrderByAttemptNoDesc(
            UUID assessmentSessionId
    );
}