
package org.optipace.garmentService.service.impl;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.optipace.garmentService.dto.request.AddGarmentBatchRequest;
import org.optipace.garmentService.dto.request.UpdateGarmentBatchRequest;
import org.optipace.garmentService.dto.response.GarmentBatchResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.entity.GarmentBatch;
import org.optipace.garmentService.exception.BadRequestException;
import org.optipace.garmentService.exception.NotFoundException;
import org.optipace.garmentService.repository.BatchRepository;
import org.optipace.garmentService.repository.GarmentBatchRepository;
import org.optipace.garmentService.service.GarmentBatchService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class GarmentBatchServiceImpl implements GarmentBatchService {

    private final GarmentBatchRepository garmentBatchRepository;
    private final BatchRepository batchRepository;

    @Override
    public SingleResponse<?> createGarmentBatch(
            AddGarmentBatchRequest request,
            String adminId) {

        log.info("Creating garment batch record by admin: {}", adminId);

        if (request == null) {
            throw new BadRequestException("Request body cannot be null");
        }

        validateRequest(
                request.getBatchId(),
                request.getProductCategoryId(),
                request.getGarmentId(),
                request.getGarmentCode()
        );

        if (!batchRepository.existsById(request.getBatchId())) {
            throw new NotFoundException(
                    "Batch not found with ID: " + request.getBatchId()
            );
        }

        String garmentCode = request.getGarmentCode().trim();

        // Garment code must be unique only within the selected batch.
        if (garmentBatchRepository
                .existsByBatchIdAndGarmentCodeIgnoreCase(
                        request.getBatchId(),
                        garmentCode)) {

            throw new BadRequestException(
                    "Garment code '" + garmentCode
                            + "' already exists in batch "
                            + request.getBatchId()
            );
        }

        GarmentBatch entity = new GarmentBatch();
        entity.setBatchId(request.getBatchId());
        entity.setProductCategoryId(request.getProductCategoryId());
        entity.setGarmentId(request.getGarmentId().trim());
        entity.setGarmentCode(garmentCode);

        garmentBatchRepository.saveAndFlush(entity);

        log.info("Garment batch record created successfully");

        return SingleResponse.success(
                "Garment batch created successfully"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public SingleResponse<PageResponse<GarmentBatchResponse>>
            getAllGarmentBatches(Pageable pageable) {

        Pageable effectivePageable = pageable;

        if (effectivePageable == null) {
            effectivePageable = PageRequest.of(
                    0,
                    10,
                    Sort.by(Sort.Direction.ASC, "garmentBatchId")
            );
        }

        Page<GarmentBatch> page =
                garmentBatchRepository.findAll(effectivePageable);

        List<GarmentBatchResponse> responseList = page.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        PageResponse<GarmentBatchResponse> pageResponse =
                new PageResponse<>(
                        responseList,
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages(),
                        page.isLast(),
                        page.isFirst(),
                        page.getNumberOfElements(),
                        page.isEmpty()
                );

        return SingleResponse.success(pageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public SingleResponse<GarmentBatchResponse> getGarmentBatchById(
            Long garmentBatchId) {

        if (garmentBatchId == null || garmentBatchId <= 0) {
            throw new BadRequestException(
                    "Garment batch ID must be a positive number"
            );
        }

        GarmentBatch entity = garmentBatchRepository
                .findById(garmentBatchId)
                .orElseThrow(() -> new NotFoundException(
                        "Garment batch not found with ID: "
                                + garmentBatchId
                ));

        return SingleResponse.success(mapToResponse(entity));
    }

    @Override
    public SingleResponse<?> updateGarmentBatch(
            Long garmentBatchId,
            UpdateGarmentBatchRequest request,
            String adminId) {

        log.info(
                "Updating garment batch record ID: {} by admin: {}",
                garmentBatchId,
                adminId
        );

        if (garmentBatchId == null || garmentBatchId <= 0) {
            throw new BadRequestException(
                    "Garment batch ID must be a positive number"
            );
        }

        if (request == null) {
            throw new BadRequestException("Request body cannot be null");
        }

        GarmentBatch existing = garmentBatchRepository
                .findById(garmentBatchId)
                .orElseThrow(() -> new NotFoundException(
                        "Garment batch not found with ID: "
                                + garmentBatchId
                ));

        validateRequest(
                request.getBatchId(),
                request.getProductCategoryId(),
                request.getGarmentId(),
                request.getGarmentCode()
        );

        if (!batchRepository.existsById(request.getBatchId())) {
            throw new NotFoundException(
                    "Batch not found with ID: " + request.getBatchId()
            );
        }

        String garmentCode = request.getGarmentCode().trim();

        // Exclude the current record when checking for duplicates.
        if (garmentBatchRepository
                .existsByBatchIdAndGarmentCodeIgnoreCaseAndGarmentBatchIdNot(
                        request.getBatchId(),
                        garmentCode,
                        garmentBatchId)) {

            throw new BadRequestException(
                    "Garment code '" + garmentCode
                            + "' already exists in batch "
                            + request.getBatchId()
            );
        }

        existing.setBatchId(request.getBatchId());
        existing.setProductCategoryId(request.getProductCategoryId());
        existing.setGarmentId(request.getGarmentId().trim());
        existing.setGarmentCode(garmentCode);

        garmentBatchRepository.saveAndFlush(existing);

        log.info(
                "Garment batch record updated successfully: {}",
                garmentBatchId
        );

        return SingleResponse.success(
                "Garment batch updated successfully"
        );
    }

    private void validateRequest(
            Long batchId,
            Long productCategoryId,
            String garmentId,
            String garmentCode) {

        if (batchId == null || batchId <= 0) {
            throw new BadRequestException(
                    "Batch ID is required and must be positive"
            );
        }

        if (productCategoryId == null || productCategoryId <= 0) {
            throw new BadRequestException(
                    "Product category ID is required and must be positive"
            );
        }

        if (garmentId == null || garmentId.isBlank()) {
            throw new BadRequestException(
                    "Garment ID is required"
            );
        }

        if (garmentCode == null || garmentCode.isBlank()) {
            throw new BadRequestException(
                    "Garment code is required"
            );
        }
    }

    private GarmentBatchResponse mapToResponse(GarmentBatch entity) {
        return new GarmentBatchResponse(
                entity.getGarmentBatchId(),
                entity.getBatchId(),
                entity.getProductCategoryId(),
                entity.getGarmentId(),
                entity.getGarmentCode()
        );
    }
}








