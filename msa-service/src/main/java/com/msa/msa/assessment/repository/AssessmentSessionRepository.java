package com.msa.msa.assessment.repository;

import com.msa.msa.assessment.entity.AssessmentSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AssessmentSessionRepository extends JpaRepository<AssessmentSession, UUID> {
    List<AssessmentSession> findByEmployeeIdAndAssessmentStatus_StatusCodeInAndRecordStatusOrderByCreatedOnDesc(Long employeeId, List<String> assigned, String a);
}