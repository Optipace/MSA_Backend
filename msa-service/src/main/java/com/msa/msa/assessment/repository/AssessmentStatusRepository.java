package com.msa.msa.assessment.repository;

import com.msa.msa.assessment.entity.AssessmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssessmentStatusRepository
        extends JpaRepository<AssessmentStatus, Long> {

    Optional<AssessmentStatus> findByStatusCode(String statusCode);
}