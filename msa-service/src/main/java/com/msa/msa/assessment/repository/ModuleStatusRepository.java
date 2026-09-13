package com.msa.msa.assessment.repository;

import com.msa.msa.assessment.entity.ModuleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModuleStatusRepository extends JpaRepository<ModuleStatus, Long> {

    Optional<ModuleStatus> findByStatusCode(String statusCode);
}