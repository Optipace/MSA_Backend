
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
                request.getGarmentName()
        );

        if (!batchRepository.existsById(request.getBatchId())) {
            throw new NotFoundException(
                    "Batch not found with ID: " + request.getBatchId()
            );
        }

        String garmentId = request.getGarmentId().trim();
        String garmentName = request.getGarmentName().trim();

        if (garmentBatchRepository
                .existsByBatchIdAndGarmentIdIgnoreCase(
                        request.getBatchId(),
                        garmentId)) {

            throw new BadRequestException(
                    "Garment ID '" + garmentId
                            + "' already exists in batch "
                            + request.getBatchId()
            );
        }

        GarmentBatch entity = new GarmentBatch();
        entity.setBatchId(request.getBatchId());
        entity.setProductCategoryId(request.getProductCategoryId());
        entity.setGarmentId(garmentId);
        entity.setGarmentName(garmentName);

        garmentBatchRepository.saveAndFlush(entity);

        log.info("Garment batch record created successfully");

        return SingleResponse.success(
                "Garment is added in batch successfully"
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
                request.getGarmentName()
        );

        if (!batchRepository.existsById(request.getBatchId())) {
            throw new NotFoundException(
                    "Batch not found with ID: " + request.getBatchId()
            );
        }

        String garmentId = request.getGarmentId().trim();

        // Prevent duplicate garment IDs within the same batch,
        // excluding the current record.
        if (garmentBatchRepository
                .existsByBatchIdAndGarmentIdIgnoreCaseAndGarmentBatchIdNot(
                        request.getBatchId(),
                        garmentId,
                        garmentBatchId)) {

            throw new BadRequestException(
                    "Garment ID '" + garmentId
                            + "' already exists in batch "
                            + request.getBatchId()
            );
        }

        existing.setBatchId(request.getBatchId());
        existing.setProductCategoryId(request.getProductCategoryId());
        existing.setGarmentId(garmentId);
        existing.setGarmentName(request.getGarmentName().trim());

        garmentBatchRepository.saveAndFlush(existing);

        log.info(
                "Garment batch record updated successfully: {}",
                garmentBatchId
        );

        return SingleResponse.success(
                "Garment batch updated successfully"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public SingleResponse<List<GarmentBatchResponse>> getGarmentsByBatchId(
            Long batchId) {

        if (batchId == null || batchId <= 0) {
            throw new BadRequestException(
                    "Batch ID must be a positive number"
            );
        }

        if (!batchRepository.existsById(batchId)) {
            throw new NotFoundException(
                    "Batch not found with ID: " + batchId
            );
        }

        List<GarmentBatchResponse> garments = garmentBatchRepository
                .findByBatchId(batchId)
                .stream()
                .map(this::mapToResponse)
                .toList();

        return SingleResponse.success(garments);
    }
    private void validateRequest(
            Long batchId,
            Long productCategoryId,
            String garmentId,
            String garmentName) {

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

        if (garmentName == null || garmentName.isBlank()) {
            throw new BadRequestException(
                    "Garment name is required"
            );
        }
    }

    private GarmentBatchResponse mapToResponse(GarmentBatch entity) {
        return new GarmentBatchResponse(
                entity.getGarmentBatchId(),
                entity.getBatchId(),
                entity.getProductCategoryId(),
                entity.getGarmentId(),
                entity.getGarmentName()
        );
    }
}
