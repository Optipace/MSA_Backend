
package org.optipace.garmentService.repository;

import java.util.List;

import org.optipace.garmentService.entity.GarmentDefectAreaSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GarmentDefectAreaSubmissionRepository
        extends JpaRepository<GarmentDefectAreaSubmission, Long> {

    List<GarmentDefectAreaSubmission> findByBatchId(Long batchId);

    List<GarmentDefectAreaSubmission> findByBatchIdAndGarmentId(
            Long batchId,
            String garmentId);

    boolean existsByBatchIdAndGarmentId(
            Long batchId,
            String garmentId);

    @Query("""
        SELECT COUNT(s) > 0
        FROM GarmentDefectAreaSubmission s
        WHERE s.batchId = :batchId
          AND s.garmentId = :garmentId
          AND (
              s.defectId = :defectId
              OR (s.defectId IS NULL AND :defectId IS NULL)
          )
          AND (
              s.areaId = :areaId
              OR (s.areaId IS NULL AND :areaId IS NULL)
          )
        """)
    boolean existsDuplicate(
            @Param("batchId") Long batchId,
            @Param("garmentId") String garmentId,
            @Param("defectId") Long defectId,
            @Param("areaId") Long areaId);
}
