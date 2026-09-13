package com.msa.msa.assessment.repository;

import com.msa.msa.assessment.entity.AssessmentTemplateRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssessmentTemplateRuleRepository
        extends JpaRepository<AssessmentTemplateRule, Long> {

    Optional<AssessmentTemplateRule> findByAssessmentTemplateIdAndRuleName(
            Long assessmentTemplateId,
            String ruleName
    );
}