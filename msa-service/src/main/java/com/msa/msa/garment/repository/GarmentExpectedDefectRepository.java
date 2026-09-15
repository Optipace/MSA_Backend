package com.msa.msa.garment.repository;

import com.msa.msa.garment.entity.GarmentExpectedDefect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GarmentExpectedDefectRepository
        extends JpaRepository<GarmentExpectedDefect, UUID> {

    List<GarmentExpectedDefect> findByGarmentInstanceId(UUID garmentInstanceId);

    List<GarmentExpectedDefect> findByGarmentInstanceIdAndGarmentAreaId(
            UUID garmentInstanceId,
            Long garmentAreaId
    );
}