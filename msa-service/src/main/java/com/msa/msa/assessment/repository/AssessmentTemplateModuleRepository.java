package com.msa.msa.assessment.repository;

import com.msa.msa.assessment.entity.AssessmentTemplateModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentTemplateModuleRepository
        extends JpaRepository<AssessmentTemplateModule, Long> {

    List<AssessmentTemplateModule> findByAssessmentTemplateIdOrderBySequenceNo(
            Long assessmentTemplateId
    );
}