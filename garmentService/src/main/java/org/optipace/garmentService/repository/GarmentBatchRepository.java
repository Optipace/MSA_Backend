
package org.optipace.garmentService.repository;

import java.util.List;


import org.optipace.garmentService.entity.GarmentBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GarmentBatchRepository
        extends JpaRepository<GarmentBatch, Long> {

    // Check whether the garment ID already exists in this batch.
    boolean existsByBatchIdAndGarmentIdIgnoreCase(
            Long batchId,
            String garmentId
    );

    // Update check: exclude the current record.
    boolean existsByBatchIdAndGarmentIdIgnoreCaseAndGarmentBatchIdNot(
            Long batchId,
            String garmentId,
            Long garmentBatchId
    );

    boolean existsByGarmentBatchId(Long garmentBatchId);

    // Fetch all garments belonging to a specific batch.
    List<GarmentBatch> findByBatchId(Long batchId);

	boolean existsByBatchIdAndGarmentId(Long batchId, String garmentId);
}
