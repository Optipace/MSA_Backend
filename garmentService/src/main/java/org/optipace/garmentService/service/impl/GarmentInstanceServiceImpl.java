package org.optipace.garmentService.service.impl;

import lombok.RequiredArgsConstructor;
import org.optipace.garmentService.dto.request.GarmentInstanceCreateRequest;
import org.optipace.garmentService.dto.request.GarmentInstanceUpdateRequest;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.entity.GarmentInstance;
import org.optipace.garmentService.exception.NotFoundException;
import org.optipace.garmentService.repository.GarmentInstanceRepository;
import org.optipace.garmentService.repository.GarmentLotRepository;
import org.optipace.garmentService.service.GarmentInstanceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GarmentInstanceServiceImpl implements GarmentInstanceService {

    private final GarmentInstanceRepository garmentInstanceRepository;

    private final GarmentLotRepository garmentLotRepository;


    // =========================================================
    // CREATE GARMENT INSTANCE
    // =========================================================

    @Override
    @Transactional
    public SingleResponse<?> createGarmentInstance(
            GarmentInstanceCreateRequest request,
            String userId) {

        // -----------------------------------------------------
        // Validate Garment Lot
        // -----------------------------------------------------

        if (!garmentLotRepository.existsById(
                request.getGarmentLotId())) {

            return new SingleResponse<>(
                    null,
                    new SingleResponse.ResponseInfo(
                            0,
                            "Garment lot not found"
                    )
            );
        }


        // -----------------------------------------------------
        // Check Serial Number
        // -----------------------------------------------------

        if (garmentInstanceRepository.existsBySerialNumber(
                request.getSerialNumber())) {

            return new SingleResponse<>(
                    null,
                    new SingleResponse.ResponseInfo(
                            0,
                            "Serial number already exists"
                    )
            );
        }


        // -----------------------------------------------------
        // Check QR Code
        // -----------------------------------------------------

        if (request.getQrCode() != null &&
                !request.getQrCode().isBlank() &&
                garmentInstanceRepository.existsByQrCode(
                        request.getQrCode())) {

            return new SingleResponse<>(
                    null,
                    new SingleResponse.ResponseInfo(
                            0,
                            "QR code already exists"
                    )
            );
        }


        // -----------------------------------------------------
        // Check Barcode
        // -----------------------------------------------------

        if (request.getBarcode() != null &&
                !request.getBarcode().isBlank() &&
                garmentInstanceRepository.existsByBarcode(
                        request.getBarcode())) {

            return new SingleResponse<>(
                    null,
                    new SingleResponse.ResponseInfo(
                            0,
                            "Barcode already exists"
                    )
            );
        }


        // -----------------------------------------------------
        // Create Entity
        // -----------------------------------------------------

        GarmentInstance garmentInstance =
                new GarmentInstance();

        garmentInstance.setSerialNumber(
                request.getSerialNumber()
        );

        garmentInstance.setQrCode(
                request.getQrCode()
        );

        garmentInstance.setBarcode(
                request.getBarcode()
        );

        garmentInstance.setCurrentStatus(
                request.getCurrentStatus() != null &&
                        !request.getCurrentStatus().isBlank()
                        ? request.getCurrentStatus()
                        : "AVAILABLE"
        );

        garmentInstance.setCurrentLocation(
                request.getCurrentLocation()
        );

        garmentInstance.setManufacturedOn(
                request.getManufacturedOn()
        );

        garmentInstance.setGarmentLotId(
                request.getGarmentLotId()
        );

        garmentInstance.setRemarks(
                request.getRemarks()
        );

        garmentInstance.setCreatedBy(
                parseUserId(userId)
        );

        garmentInstance.setRecordStatus('A');


        // -----------------------------------------------------
        // Save
        // -----------------------------------------------------

        GarmentInstance savedInstance =
                garmentInstanceRepository.save(
                        garmentInstance
                );


        return new SingleResponse<>(
                savedInstance,
                new SingleResponse.ResponseInfo(
                        1,
                        "Garment instance created successfully"
                )
        );
    }


    // =========================================================
    // GET BY SERIAL NUMBER
    // =========================================================

    @Override
    public SingleResponse<?> getGarmentInstance(
            String serialNumber) {

        GarmentInstance garmentInstance =
                garmentInstanceRepository
                        .findBySerialNumber(serialNumber)
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Garment instance not found"
                                )
                        );


        return new SingleResponse<>(
                garmentInstance,
                new SingleResponse.ResponseInfo(
                        1,
                        "Garment instance fetched successfully"
                )
        );
    }


    // =========================================================
    // GET ALL GARMENT INSTANCES
    // =========================================================

    @Override
    public SingleResponse<?> getAllGarmentInstances(
            Pageable pageable) {

        Page<GarmentInstance> page =
                garmentInstanceRepository.findByRecordStatus(
                        "A",
                        pageable
                );


        return new SingleResponse<>(
                page,
                new SingleResponse.ResponseInfo(
                        1,
                        "Garment instances fetched successfully"
                )
        );
    }


    // =========================================================
    // UPDATE GARMENT INSTANCE
    // =========================================================

    @Override
    @Transactional
    public SingleResponse<?> updateGarmentInstance(
            String serialNumber,
            GarmentInstanceUpdateRequest request,
            String userId) {

        // -----------------------------------------------------
        // Find Existing Instance
        // -----------------------------------------------------

        GarmentInstance garmentInstance =
                garmentInstanceRepository
                        .findBySerialNumber(serialNumber)
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Garment instance not found"
                                )
                        );


        // -----------------------------------------------------
        // Serial Number
        // -----------------------------------------------------

        if (request.getSerialNumber() != null &&
                !request.getSerialNumber().isBlank() &&
                !request.getSerialNumber()
                        .equals(garmentInstance.getSerialNumber())) {

            if (garmentInstanceRepository
                    .existsBySerialNumberAndGarmentInstanceIdNot(
                            request.getSerialNumber(),
                            garmentInstance.getGarmentInstanceId())) {

                return new SingleResponse<>(
                        null,
                        new SingleResponse.ResponseInfo(
                                0,
                                "Serial number already exists"
                        )
                );
            }

            garmentInstance.setSerialNumber(
                    request.getSerialNumber()
            );
        }


        // -----------------------------------------------------
        // QR Code
        // -----------------------------------------------------

        if (request.getQrCode() != null &&
                !request.getQrCode().isBlank() &&
                !request.getQrCode()
                        .equals(garmentInstance.getQrCode())) {

            if (garmentInstanceRepository
                    .existsByQrCodeAndGarmentInstanceIdNot(
                            request.getQrCode(),
                            garmentInstance.getGarmentInstanceId())) {

                return new SingleResponse<>(
                        null,
                        new SingleResponse.ResponseInfo(
                                0,
                                "QR code already exists"
                        )
                );
            }

            garmentInstance.setQrCode(
                    request.getQrCode()
            );
        }


        // -----------------------------------------------------
        // Barcode
        // -----------------------------------------------------

        if (request.getBarcode() != null &&
                !request.getBarcode().isBlank() &&
                !request.getBarcode()
                        .equals(garmentInstance.getBarcode())) {

            if (garmentInstanceRepository
                    .existsByBarcodeAndGarmentInstanceIdNot(
                            request.getBarcode(),
                            garmentInstance.getGarmentInstanceId())) {

                return new SingleResponse<>(
                        null,
                        new SingleResponse.ResponseInfo(
                                0,
                                "Barcode already exists"
                        )
                );
            }

            garmentInstance.setBarcode(
                    request.getBarcode()
            );
        }


        // -----------------------------------------------------
        // Garment Lot Validation
        // -----------------------------------------------------

        if (request.getGarmentLotId() != null &&
                !request.getGarmentLotId()
                        .equals(garmentInstance.getGarmentLotId())) {

            if (!garmentLotRepository.existsById(
                    request.getGarmentLotId())) {

                return new SingleResponse<>(
                        null,
                        new SingleResponse.ResponseInfo(
                                0,
                                "Garment lot not found"
                        )
                );
            }

            garmentInstance.setGarmentLotId(
                    request.getGarmentLotId()
            );
        }


        // -----------------------------------------------------
        // Current Status
        // -----------------------------------------------------

        if (request.getCurrentStatus() != null &&
                !request.getCurrentStatus().isBlank()) {

            garmentInstance.setCurrentStatus(
                    request.getCurrentStatus()
            );
        }


        // -----------------------------------------------------
        // Current Location
        // -----------------------------------------------------

        if (request.getCurrentLocation() != null) {

            garmentInstance.setCurrentLocation(
                    request.getCurrentLocation()
            );
        }


        // -----------------------------------------------------
        // Manufactured On
        // -----------------------------------------------------

        if (request.getManufacturedOn() != null) {

            garmentInstance.setManufacturedOn(
                    request.getManufacturedOn()
            );
        }


        // -----------------------------------------------------
        // Remarks
        // -----------------------------------------------------

        if (request.getRemarks() != null) {

            garmentInstance.setRemarks(
                    request.getRemarks()
            );
        }


        // -----------------------------------------------------
        // Record Status
        // -----------------------------------------------------

        if (request.getRecordStatus() != null &&
                !request.getRecordStatus().isBlank()) {

            garmentInstance.setRecordStatus(
                    request.getRecordStatus().charAt(0)
            );
        }


        // -----------------------------------------------------
        // Audit
        // -----------------------------------------------------

        garmentInstance.setUpdatedBy(
                parseUserId(userId)
        );


        // -----------------------------------------------------
        // Save
        // -----------------------------------------------------

        GarmentInstance updatedInstance =
                garmentInstanceRepository.save(
                        garmentInstance
                );


        return new SingleResponse<>(
                updatedInstance,
                new SingleResponse.ResponseInfo(
                        1,
                        "Garment instance updated successfully"
                )
        );
    }


    // =========================================================
    // DELETE GARMENT INSTANCE
    // =========================================================

    @Override
    @Transactional
    public SingleResponse<?> deleteGarmentInstance(
            String serialNumber,
            String userId) {

        GarmentInstance garmentInstance =
                garmentInstanceRepository
                        .findBySerialNumber(serialNumber)
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Garment instance not found"
                                )
                        );


        // -----------------------------------------------------
        // Soft Delete
        // -----------------------------------------------------

        garmentInstance.setRecordStatus('I');


        garmentInstance.setUpdatedBy(
                parseUserId(userId)
        );


        garmentInstanceRepository.save(
                garmentInstance
        );


        return new SingleResponse<>(
                null,
                new SingleResponse.ResponseInfo(
                        1,
                        "Garment instance deleted successfully"
                )
        );
    }


    // =========================================================
    // PARSE USER ID
    // =========================================================

    private Long parseUserId(String userId) {

        if (userId == null || userId.isBlank()) {
            return null;
        }

        try {
            return Long.valueOf(userId);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}