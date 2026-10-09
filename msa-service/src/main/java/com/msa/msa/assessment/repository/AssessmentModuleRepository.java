package com.msa.msa.assessment.repository;

import com.msa.msa.assessment.entity.AssessmentModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AssessmentModuleRepository
        extends JpaRepository<AssessmentModule, Long> {

    Optional<AssessmentModule> findByModuleCode(String moduleCode);

    @Query("SELECT COALESCE(MAX(m.displayOrder), 0) FROM AssessmentModule m")
    Integer findMaxDisplayOrder();

    boolean existsByModuleCodeAndRecordStatus(String moduleCode, Character recordStatus);
}