package org.optipace.garmentService.repository;

import java.util.List;
import java.util.Optional;

import org.optipace.garmentService.entity.GarmentBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GarmentBatchRepository
        extends JpaRepository<GarmentBatch, Long> {

    boolean existsByBatchIdAndGarmentIdIgnoreCase(
            Long batchId,
            String garmentId);

    boolean existsByBatchIdAndGarmentIdIgnoreCaseAndGarmentBatchIdNot(
            Long batchId,
            String garmentId,
            Long garmentBatchId);

    boolean existsByGarmentBatchId(Long garmentBatchId);

    List<GarmentBatch> findByBatchId(Long batchId);

    // Optional: keep only if case-sensitive checking is required.
    boolean existsByBatchIdAndGarmentId(
            Long batchId,
            String garmentId);
    
    // New method
    Optional<GarmentBatch> findByBatchIdAndGarmentIdIgnoreCase(
            Long batchId, String garmentId);
}