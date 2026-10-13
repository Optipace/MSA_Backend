
package org.optipace.garmentService.repository;

import org.optipace.garmentService.entity.GarmentBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GarmentBatchRepository
        extends JpaRepository<GarmentBatch, Long> {

    // Check whether the garment code already exists in this batch.
    boolean existsByBatchIdAndGarmentCodeIgnoreCase(
            Long batchId,
            String garmentCode
    );

    // Update check: exclude the current record.
    boolean existsByBatchIdAndGarmentCodeIgnoreCaseAndGarmentBatchIdNot(
            Long batchId,
            String garmentCode,
            Long garmentBatchId
    );
}
