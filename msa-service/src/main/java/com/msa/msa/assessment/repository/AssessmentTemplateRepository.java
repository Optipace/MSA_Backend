package com.msa.msa.assessment.repository;

import com.msa.msa.assessment.entity.AssessmentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssessmentTemplateRepository
        extends JpaRepository<AssessmentTemplate, Long> {

    Optional<AssessmentTemplate> findByTemplateCode(String templateCode);
}