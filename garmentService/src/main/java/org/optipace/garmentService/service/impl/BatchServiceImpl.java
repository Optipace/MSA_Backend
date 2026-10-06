package org.optipace.garmentService.service.impl;

import org.optipace.garmentService.dto.request.AddBatchRequest;
import org.optipace.garmentService.dto.request.UpdateBatchRequest;
import org.optipace.garmentService.dto.response.BatchResponse;
import org.optipace.garmentService.dto.response.ListOfBatchResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.entity.Batch;
import org.optipace.garmentService.exception.BadRequestException;
import org.optipace.garmentService.exception.NotFoundException;
import org.optipace.garmentService.repository.BatchRepository;
import org.optipace.garmentService.service.BatchService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class BatchServiceImpl implements BatchService {

    // ⚠️ char literals — NOT "A" / "I"
    private static final Character STATUS_ACTIVE   = 'A';
    private static final Character STATUS_INACTIVE = 'D';

    private final BatchRepository batchRepository;

    // ---------- CREATE ----------

    @Override
    @Transactional
    public SingleResponse<?> createBatch(AddBatchRequest request, String adminId) {

        log.info("Initiating batch creation for name: {} by Admin: {}",
                request.getBatchName(), adminId);

        String batchName = null;

        if (request.getBatchName() != null && !request.getBatchName().trim().isEmpty()) {

            batchName = request.getBatchName().trim();

            if (batchRepository.existsByBatchNameAndRecordStatus(batchName, STATUS_ACTIVE)) {

                throw new BadRequestException(
                        "Batch " + batchName + " already exists");
            }
        }

        String batchCode = null;

        if (request.getBatchCode() != null && !request.getBatchCode().trim().isEmpty()) {
            batchCode = request.getBatchCode().trim().toUpperCase();
        }

        Batch batch = new Batch();
        batch.setBatchName(batchName);
        batch.setBatchCode(batchCode);
        batch.setDescription(request.getDescription());
        batch.setDisplayOrder(request.getDisplayOrder());
        batch.setRecordStatus(STATUS_ACTIVE);
        batch.setCreatedBy(adminId);
        batch.setUpdatedBy(adminId);

        Batch saved = batchRepository.save(batch);

        log.info("Batch successfully created with ID: {}", saved.getBatchId());

        return SingleResponse.success("Batch created successfully"
                + (saved.getBatchName() != null ? " with name: " + saved.getBatchName() : ""));
    }

    // ---------- GET ALL ----------

    @Override
    @Transactional(readOnly = true)
    public SingleResponse<PageResponse<ListOfBatchResponse>> getAllBatches(Pageable pageable) {

        Pageable sortedPageable = pageable.getSort().isSorted()
                ? pageable
                : PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by(Sort.Order.asc("displayOrder").nullsLast(),
                                Sort.Order.asc("batchName").nullsLast()));

        Page<Batch> batchPage = batchRepository.findByRecordStatus(STATUS_ACTIVE, sortedPageable);

        PageResponse<ListOfBatchResponse> pageResponse =
                PageResponse.of(batchPage.map(this::mapToListResponse));

        return SingleResponse.success(pageResponse);
    }

    // ---------- GET BY ID ----------

    @Override
    @Transactional(readOnly = true)
    public SingleResponse<BatchResponse> getBatchById(Long batchId) {

        Batch batch = batchRepository
                .findByBatchIdAndRecordStatus(batchId, STATUS_ACTIVE)
                .orElseThrow(() -> new NotFoundException("Batch not found with ID: " + batchId));

        return SingleResponse.success(mapToResponse(batch));
    }

    // ---------- UPDATE ----------

    @Override
    @Transactional
    public SingleResponse<?> updateBatch(Long batchId, UpdateBatchRequest request, String adminId) {

        log.info("Initiating batch update for ID: {} by Admin: {}", batchId, adminId);

        Batch batch = batchRepository
                .findByBatchIdAndRecordStatus(batchId, STATUS_ACTIVE)
                .orElseThrow(() -> new NotFoundException("Batch not found with ID: " + batchId));

        if (request.getBatchName() != null && !request.getBatchName().trim().isEmpty()) {

            String batchName = request.getBatchName().trim();

            if (batchRepository.existsByBatchNameAndBatchIdNotAndRecordStatus(
                    batchName, batchId, STATUS_ACTIVE)) {

                throw new BadRequestException(
                        "Batch " + batchName + " already exists");
            }

            batch.setBatchName(batchName);
        }

        if (request.getBatchCode() != null && !request.getBatchCode().trim().isEmpty()) {
            batch.setBatchCode(request.getBatchCode().trim().toUpperCase());
        }

        if (request.getDescription()  != null) batch.setDescription(request.getDescription());
        if (request.getDisplayOrder() != null) batch.setDisplayOrder(request.getDisplayOrder());

        batch.setUpdatedBy(adminId);

        batchRepository.save(batch);

        log.info("Batch successfully updated with ID: {}", batchId);

        return SingleResponse.success("Batch updated successfully");
    }

    // ---------- DELETE (soft) ----------

    @Override
    @Transactional
    public SingleResponse<?> deleteBatchById(Long batchId, String adminId) {

        log.info("Initiating batch deletion for ID: {} by Admin: {}", batchId, adminId);

        Batch batch = batchRepository
                .findByBatchIdAndRecordStatus(batchId, STATUS_ACTIVE)
                .orElseThrow(() -> new NotFoundException("Batch not found with ID: " + batchId));

        batch.setRecordStatus(STATUS_INACTIVE);
        batch.setUpdatedBy(adminId);
        batchRepository.save(batch);

        log.info("Batch successfully deleted with ID: {}", batchId);

        return SingleResponse.success("Batch deleted successfully");
    }

    // ---------- MAPPERS ----------

    private BatchResponse mapToResponse(Batch b) {

        BatchResponse r = new BatchResponse();
        r.setBatchId(b.getBatchId());
        r.setBatchName(b.getBatchName());
        r.setBatchCode(b.getBatchCode());
        r.setDescription(b.getDescription());
        r.setDisplayOrder(b.getDisplayOrder());
        r.setRecordStatus(b.getRecordStatus() == null
                ? null : String.valueOf(b.getRecordStatus()));
        r.setCreatedBy(b.getCreatedBy());
        r.setUpdatedBy(b.getUpdatedBy());
        r.setCreatedOn(b.getCreatedOn());
        r.setUpdatedOn(b.getUpdatedOn());
        return r;
    }

    private ListOfBatchResponse mapToListResponse(Batch b) {
        return new ListOfBatchResponse(
                b.getBatchId(),
                b.getBatchName(),
                b.getBatchCode(),
                b.getDisplayOrder()
        );
    }
}