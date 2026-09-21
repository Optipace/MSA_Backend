package com.msa.msa.assessment.repository;

import com.msa.msa.assessment.entity.AssessmentSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssessmentSessionRepository
        extends JpaRepository<AssessmentSession, UUID> {
}