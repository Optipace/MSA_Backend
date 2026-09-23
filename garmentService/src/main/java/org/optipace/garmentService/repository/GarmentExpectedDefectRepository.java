package org.optipace.garmentService.repository;

import org.optipace.garmentService.entity.GarmentExpectedDefect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GarmentExpectedDefectRepository
        extends JpaRepository<GarmentExpectedDefect, UUID> {

    boolean existsByGarmentInstanceIdAndGarmentAreaIdAndDefectId(
            UUID garmentInstanceId,
            Long garmentAreaId,
            Long defectId
    );

    Optional<GarmentExpectedDefect> findByDefectId(Long defectId);
}