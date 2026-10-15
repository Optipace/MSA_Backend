
package org.optipace.garmentService.repository;

import java.util.List;
import java.util.Optional;

import org.optipace.garmentService.entity.GarmentAreaDefect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GarmentAreaDefectRepository
        extends JpaRepository<GarmentAreaDefect, Long> {

    boolean existsByGarmentBatchIdAndGarmentAreaIdAndDefectId(
            Long garmentBatchId,
            Long garmentAreaId,
            Long defectId);

    Optional<GarmentAreaDefect> findByGarmentBatchIdAndGarmentAreaIdAndDefectId(
            Long garmentBatchId,
            Long garmentAreaId,
            Long defectId);

    boolean existsByGarmentBatchIdAndGarmentAreaIdAndDefectIdAndGarmentAreaDefectIdNot(
            Long garmentBatchId,
            Long garmentAreaId,
            Long defectId,
            Long garmentAreaDefectId);
    
    boolean existsByGarmentBatchId(Long garmentBatchId);
    
    Page<GarmentAreaDefect> findByRecordStatusIn(
            List<Character> recordStatuses,
            Pageable pageable
    );

	Optional<GarmentAreaDefect> findByGarmentAreaDefectIdAndRecordStatus(Long garmentAreaDefectId, char c);
}
