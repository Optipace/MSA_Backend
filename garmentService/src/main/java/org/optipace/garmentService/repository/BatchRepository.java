package org.optipace.garmentService.repository;

import java.util.Optional;

import org.optipace.garmentService.entity.Batch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {

    Page<Batch> findByRecordStatus(Character recordStatus, Pageable pageable);

    Optional<Batch> findByBatchIdAndRecordStatus(Long batchId, Character recordStatus);

    boolean existsByBatchNameAndRecordStatus(String batchName, Character recordStatus);

    boolean existsByBatchNameAndBatchIdNotAndRecordStatus(
            String batchName, Long batchId, Character recordStatus);
}