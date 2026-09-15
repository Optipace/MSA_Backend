package com.msa.msa.garment.repository;

import com.msa.msa.garment.entity.GarmentIdentificationResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GarmentIdentificationResponseRepository
        extends JpaRepository<GarmentIdentificationResponse, UUID> {

    List<GarmentIdentificationResponse> findByAssessmentResponseId(
            UUID assessmentResponseId
    );

    List<GarmentIdentificationResponse> findByGarmentInstanceId(
            UUID garmentInstanceId
    );
}