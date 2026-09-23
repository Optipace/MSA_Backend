package org.optipace.garmentService.repository;

import org.optipace.garmentService.entity.GarmentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GarmentTypeRepository extends JpaRepository<GarmentType, Long> {

    Page<GarmentType> findByRecordStatus(String recordStatus, Pageable pageable);

    Optional<GarmentType> findByGarmentTypeIdAndRecordStatus(
            Long garmentTypeId,
            String recordStatus
    );

    boolean existsByGarmentCodeAndRecordStatus(
            String garmentCode,
            String recordStatus
    );

    boolean existsByGarmentCodeAndGarmentTypeIdNotAndRecordStatus(
            String garmentCode,
            Long garmentTypeId,
            String recordStatus
    );

	boolean existsByGarmentCode(String garmentCode);
}