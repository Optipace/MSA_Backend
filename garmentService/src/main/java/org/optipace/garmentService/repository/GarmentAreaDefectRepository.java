package org.optipace.garmentService.repository;

import org.optipace.garmentService.entity.GarmentAreaDefect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GarmentAreaDefectRepository
        extends JpaRepository<GarmentAreaDefect, Long> {

    boolean existsByGarmentAreaIdAndDefectId(
            Long garmentAreaId,
            Long defectId
    );

    Optional<GarmentAreaDefect> findByGarmentAreaIdAndDefectId(
            Long garmentAreaId,
            Long defectId
    );

    boolean existsByGarmentAreaIdAndDefectIdAndGarmentAreaDefectIdNot(
            Long garmentAreaId,
            Long defectId,
            Long garmentAreaDefectId
    );
}