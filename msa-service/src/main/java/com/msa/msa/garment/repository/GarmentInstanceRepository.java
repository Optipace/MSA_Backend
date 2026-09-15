package com.msa.msa.garment.repository;

import com.msa.msa.garment.entity.GarmentInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GarmentInstanceRepository extends JpaRepository<GarmentInstance, UUID> {

    Optional<GarmentInstance> findByBarcode(String barcode);
}