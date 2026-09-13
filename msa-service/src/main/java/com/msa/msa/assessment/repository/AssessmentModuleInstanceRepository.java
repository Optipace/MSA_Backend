package com.msa.msa.assessment.repository;

import com.msa.msa.assessment.entity.AssessmentModuleInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssessmentModuleInstanceRepository
        extends JpaRepository<AssessmentModuleInstance, UUID> {

    List<AssessmentModuleInstance>
    findByAssessmentAttemptAssessmentAttemptIdOrderBySequenceNo(
            UUID assessmentAttemptId
    );

    Optional<AssessmentModuleInstance>
    findByAssessmentModuleInstanceIdAndAssessmentAttemptAssessmentSessionAssessmentSessionId(
            UUID assessmentModuleInstanceId,
            UUID assessmentSessionId
    );
}