package org.optipace.garmentService.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.optipace.garmentService.dto.request.GarmentDefectSubmissionRequest;
import org.optipace.garmentService.dto.response.GarmentDefectSubmissionResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.entity.GarmentExpectedDefect;
import org.optipace.garmentService.entity.GarmentInstance;
import org.optipace.garmentService.exception.BadRequestException;
import org.optipace.garmentService.repository.GarmentExpectedDefectRepository;
import org.optipace.garmentService.repository.GarmentInstanceRepository;
import org.optipace.garmentService.service.GarmentDefectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class GarmentDefectServiceImpl implements GarmentDefectService {

    private static final Character STATUS_ACTIVE = 'A';
    private static final String DEFAULT_STATUS = "AVAILABLE";

    private final GarmentInstanceRepository garmentInstanceRepository;
    private final GarmentExpectedDefectRepository garmentExpectedDefectRepository;

    @Override
    @Transactional
    public SingleResponse<GarmentDefectSubmissionResponse> submitGarmentDefects(
            GarmentDefectSubmissionRequest request, String adminId) {

        log.info("Submitting garment defects: identifier={} batch={} lot={} admin={}",
                request.getGarmentIdentifier(), request.getBatchId(),
                request.getGarmentLotId(), adminId);

        // created_by / updated_by are INT8 in the DB, so parse the string adminId
        Long adminUserId = parseAdminId(adminId);

        String identifier = request.getGarmentIdentifier().trim();

        // Duplicate check on serial number (unique in DB)
        if (garmentInstanceRepository.existsBySerialNumber(identifier)) {
            throw new BadRequestException("Garment " + identifier + " already exists");
        }

        // Build garment_instance
        GarmentInstance instance = new GarmentInstance();
        instance.setSerialNumber(identifier);              // form input → serial_number
        instance.setBatchId(request.getBatchId());
        instance.setGarmentLotId(request.getGarmentLotId());
        instance.setCurrentStatus(DEFAULT_STATUS);         // "AVAILABLE"
        instance.setCurrentLocation(request.getCurrentLocation());
        instance.setRemarks(request.getRemarks());
        instance.setRecordStatus(STATUS_ACTIVE);
        instance.setCreatedBy(adminUserId);
        instance.setUpdatedBy(adminUserId);

        GarmentInstance savedInstance = garmentInstanceRepository.save(instance);

        // Flatten (defect + area) → one row each
        List<GarmentExpectedDefect> rows = new ArrayList<>();

        for (GarmentDefectSubmissionRequest.DefectSelection d : request.getDefects()) {

            if (d.getAreaIds() == null || d.getAreaIds().isEmpty()) {
                throw new BadRequestException(
                        "Defect id " + d.getDefectId() + " has no areas selected");
            }

            for (Long areaId : d.getAreaIds()) {

                GarmentExpectedDefect row = new GarmentExpectedDefect();
                row.setGarmentInstanceId(savedInstance.getGarmentInstanceId());
                row.setDefectId(d.getDefectId());
                row.setGarmentAreaId(areaId);
                row.setSeverityLevelId(d.getSeverityLevelId());

                rows.add(row);
            }
        }

        garmentExpectedDefectRepository.saveAll(rows);

        log.info("Saved garment instance {} with {} defect rows",
                savedInstance.getGarmentInstanceId(), rows.size());

        GarmentDefectSubmissionResponse response = new GarmentDefectSubmissionResponse(
                savedInstance.getGarmentInstanceId(),
                savedInstance.getSerialNumber(),
                savedInstance.getBatchId(),
                rows.size()
        );

        return SingleResponse.success(response);
    }

    private Long parseAdminId(String adminId) {
        if (adminId == null) return null;
        try {
            return Long.parseLong(adminId.trim());
        } catch (NumberFormatException e) {
            log.warn("X-User-Id '{}' is not numeric; storing as null", adminId);
            return null;
        }
    }
}