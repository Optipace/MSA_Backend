
package org.optipace.garmentService.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.optipace.garmentService.dto.request.GarmentDefectAreaSubmissionRequest;
import org.optipace.garmentService.dto.request.GarmentDefectAreaSubmissionRequest.DefectSubmissionRequest;
import org.optipace.garmentService.dto.request.GarmentDefectAreaSubmissionRequest.GarmentSubmissionRequest;
import org.optipace.garmentService.dto.response.GarmentDefectAreaSubmissionResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.entity.GarmentDefectAreaSubmission;
import org.optipace.garmentService.exception.BadRequestException;
import org.optipace.garmentService.repository.BatchRepository;
import org.optipace.garmentService.repository.DefectMasterRepository;
import org.optipace.garmentService.repository.GarmentAreaRepository;
import org.optipace.garmentService.repository.GarmentBatchRepository;
import org.optipace.garmentService.repository.GarmentDefectAreaSubmissionRepository;
import org.optipace.garmentService.service.GarmentDefectAreaSubmissionService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GarmentDefectAreaSubmissionServiceImpl
        implements GarmentDefectAreaSubmissionService {

    private final GarmentDefectAreaSubmissionRepository submissionRepository;
    private final BatchRepository batchRepository;
    private final GarmentBatchRepository garmentBatchRepository;
    private final DefectMasterRepository defectMasterRepository;
    private final GarmentAreaRepository garmentAreaRepository;

    @Override
    @Transactional
    public SingleResponse<List<GarmentDefectAreaSubmissionResponse>> submit(
            GarmentDefectAreaSubmissionRequest request,
            String userId) {

        if (request == null) {
            throw new BadRequestException(
                    "Submission request is required");
        }

        validateSubmission(request);

        Long batchId = request.getBatchId();

        List<GarmentDefectAreaSubmission> entities = new ArrayList<>();

        for (GarmentSubmissionRequest garment : request.getGarments()) {

            String garmentId = garment.getGarmentId().trim();

            for (DefectSubmissionRequest defect : garment.getDefects()) {

                Long defectId = defect.getDefectId();
                List<Long> areaIds = defect.getAreaIds();

                // No area selected: save one record with areaId = null,
                // only when this association does not already exist.
                if (areaIds == null || areaIds.isEmpty()) {

                    addIfNotExists(
                            batchId,
                            garmentId,
                            defectId,
                            null,
                            userId,
                            entities);

                    continue;
                }

                // Save only new defect-area combinations.
                for (Long areaId : areaIds) {

                    addIfNotExists(
                            batchId,
                            garmentId,
                            defectId,
                            areaId,
                            userId,
                            entities);
                }
            }
        }

        List<GarmentDefectAreaSubmission> saved =
                entities.isEmpty()
                        ? new ArrayList<>()
                        : submissionRepository.saveAllAndFlush(entities);

        List<GarmentDefectAreaSubmissionResponse> result =
                saved.stream()
                        .map(this::toResponse)
                        .toList();

        SingleResponse<List<GarmentDefectAreaSubmissionResponse>> response =
                new SingleResponse<>();

        response.setData(result);

        return response;
    }

    private void addIfNotExists(
            Long batchId,
            String garmentId,
            Long defectId,
            Long areaId,
            String userId,
            List<GarmentDefectAreaSubmission> pendingEntities) {

        // Skip if this exact association is already in the database.
        if (submissionRepository.existsDuplicate(
                batchId, garmentId, defectId, areaId)) {
            return;
        }

        // Skip duplicate combinations repeated in the same request.
        boolean duplicateInRequest = pendingEntities.stream()
                .anyMatch(entity ->
                        Objects.equals(entity.getBatchId(), batchId)
                        && Objects.equals(entity.getGarmentId(), garmentId)
                        && Objects.equals(entity.getDefectId(), defectId)
                        && Objects.equals(entity.getAreaId(), areaId));

        if (duplicateInRequest) {
            return;
        }

        pendingEntities.add(
                createEntity(batchId, garmentId, defectId, areaId, userId));
    }

    private GarmentDefectAreaSubmission createEntity(
            Long batchId,
            String garmentId,
            Long defectId,
            Long areaId,
            String userId) {

        GarmentDefectAreaSubmission entity =
                new GarmentDefectAreaSubmission();

        entity.setBatchId(batchId);
        entity.setGarmentId(garmentId);
        entity.setDefectId(defectId);
        entity.setAreaId(areaId);
        entity.setCreatedBy(userId);

        return entity;
    }

    private void validateSubmission(
            GarmentDefectAreaSubmissionRequest request) {

        Long batchId = request.getBatchId();

        if (batchId == null || batchId <= 0) {
            throw new BadRequestException(
                    "Batch ID must be greater than zero");
        }

        if (!batchRepository.existsById(batchId)) {
            throw new BadRequestException(
                    "Batch not found with ID: " + batchId);
        }

        if (request.getGarments() == null
                || request.getGarments().isEmpty()) {
            throw new BadRequestException(
                    "At least one garment is required");
        }

        for (GarmentSubmissionRequest garment : request.getGarments()) {

            if (garment == null
                    || garment.getGarmentId() == null
                    || garment.getGarmentId().isBlank()) {
                throw new BadRequestException(
                        "Garment ID is required");
            }

            String garmentId = garment.getGarmentId().trim();

            // The garment must already be associated with this batch.
            if (!garmentBatchRepository
                    .existsByBatchIdAndGarmentId(batchId, garmentId)) {
                throw new BadRequestException(
                        "Garment ID " + garmentId
                        + " not found in batch " + batchId);
            }

            if (garment.getDefects() == null
                    || garment.getDefects().isEmpty()) {
                throw new BadRequestException(
                        "At least one defect entry is required for garment "
                        + garmentId);
            }

            for (DefectSubmissionRequest defect : garment.getDefects()) {

                if (defect == null) {
                    throw new BadRequestException(
                            "Defect entry cannot be null");
                }

                Long defectId = defect.getDefectId();

                // A null defect ID is allowed.
                if (defectId != null) {

                    if (defectId <= 0) {
                        throw new BadRequestException(
                                "Defect ID must be greater than zero");
                    }

                    if (!defectMasterRepository.existsById(defectId)) {
                        throw new BadRequestException(
                                "Defect ID " + defectId
                                + " not found in defect master");
                    }
                }

                List<Long> areaIds = defect.getAreaIds();

                if (areaIds != null) {
                    for (Long areaId : areaIds) {

                        // Null area entries are allowed.
                        if (areaId == null) {
                            continue;
                        }

                        if (areaId <= 0) {
                            throw new BadRequestException(
                                    "Area ID must be greater than zero");
                        }

                        if (!garmentAreaRepository.existsById(areaId)) {
                            throw new BadRequestException(
                                    "Area ID " + areaId
                                    + " not found in garment area");
                        }
                    }
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SingleResponse<List<GarmentDefectAreaSubmissionResponse>>
            getByBatchId(Long batchId) {

        if (batchId == null || batchId <= 0) {
            throw new BadRequestException(
                    "Batch ID must be greater than zero");
        }

        if (!batchRepository.existsById(batchId)) {
            throw new BadRequestException(
                    "Batch not found with ID: " + batchId);
        }

        List<GarmentDefectAreaSubmissionResponse> result =
                submissionRepository.findByBatchId(batchId)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        SingleResponse<List<GarmentDefectAreaSubmissionResponse>> response =
                new SingleResponse<>();

        response.setData(result);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public SingleResponse<List<GarmentDefectAreaSubmissionResponse>>
            getByBatchIdAndGarmentId(Long batchId, String garmentId) {

        if (batchId == null || batchId <= 0) {
            throw new BadRequestException(
                    "Batch ID must be greater than zero");
        }

        if (!batchRepository.existsById(batchId)) {
            throw new BadRequestException(
                    "Batch not found with ID: " + batchId);
        }

        if (garmentId == null || garmentId.isBlank()) {
            throw new BadRequestException(
                    "Garment ID is required");
        }

        String normalizedGarmentId = garmentId.trim();

        if (!garmentBatchRepository.existsByBatchIdAndGarmentId(
                batchId, normalizedGarmentId)) {
            throw new BadRequestException(
                    "Garment ID " + normalizedGarmentId
                    + " not found in batch " + batchId);
        }

        List<GarmentDefectAreaSubmissionResponse> result =
                submissionRepository.findByBatchIdAndGarmentId(
                                batchId, normalizedGarmentId)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        SingleResponse<List<GarmentDefectAreaSubmissionResponse>> response =
                new SingleResponse<>();

        response.setData(result);

        return response;
    }

    private GarmentDefectAreaSubmissionResponse toResponse(
            GarmentDefectAreaSubmission entity) {

        GarmentDefectAreaSubmissionResponse response =
                new GarmentDefectAreaSubmissionResponse();

        response.setSubmissionDetailId(entity.getSubmissionDetailId());
        response.setBatchId(entity.getBatchId());
        response.setGarmentId(entity.getGarmentId());
        response.setDefectId(entity.getDefectId());
        response.setAreaId(entity.getAreaId());
        response.setCreatedBy(entity.getCreatedBy());
        response.setCreatedOn(entity.getCreatedOn());

        return response;
    }
}
