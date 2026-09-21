package org.optipace.garmentService.repository;

import org.optipace.garmentService.entity.GarmentInstance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GarmentInstanceRepository extends JpaRepository<GarmentInstance, UUID> {

    Optional<GarmentInstance> findBySerialNumber(String serialNumber);

    boolean existsBySerialNumber(String serialNumber);

    boolean existsByQrCode(String qrCode);

    boolean existsByBarcode(String barcode);

    boolean existsBySerialNumberAndGarmentInstanceIdNot(
            String serialNumber,
            UUID garmentInstanceId
    );

    boolean existsByQrCodeAndGarmentInstanceIdNot(
            String qrCode,
            UUID garmentInstanceId
    );

    boolean existsByBarcodeAndGarmentInstanceIdNot(
            String barcode,
            UUID garmentInstanceId
    );

    Page<GarmentInstance> findByRecordStatus(
            String recordStatus,
            Pageable pageable
    );
}