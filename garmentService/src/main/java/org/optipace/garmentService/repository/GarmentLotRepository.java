package org.optipace.garmentService.repository;

import org.optipace.garmentService.entity.GarmentLot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GarmentLotRepository extends JpaRepository<GarmentLot, UUID> {

    Page<GarmentLot> findByRecordStatus(Character recordStatus, Pageable pageable);

    boolean existsByLotNumber(String lotNumber);

    Optional<GarmentLot> findByLotNumber(String lotNumber);
}