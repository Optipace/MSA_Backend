package com.msa.msa.assessment.repository;

import com.msa.msa.assessment.entity.AssessmentModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssessmentModuleRepository
        extends JpaRepository<AssessmentModule, Long> {

    Optional<AssessmentModule> findByModuleCode(String moduleCode);
}