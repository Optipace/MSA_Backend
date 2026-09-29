package org.optipace.garmentService.service;

import org.optipace.garmentService.dto.request.AddBatchRequest;
import org.optipace.garmentService.dto.request.UpdateBatchRequest;
import org.optipace.garmentService.dto.response.BatchResponse;
import org.optipace.garmentService.dto.response.ListOfBatchResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.springframework.data.domain.Pageable;

public interface BatchService {

    SingleResponse<?> createBatch(AddBatchRequest request, String adminId);

    SingleResponse<PageResponse<ListOfBatchResponse>> getAllBatches(Pageable pageable);

    SingleResponse<BatchResponse> getBatchById(Long batchId);

    SingleResponse<?> updateBatch(Long batchId, UpdateBatchRequest request, String adminId);

    SingleResponse<?> deleteBatchById(Long batchId, String adminId);
}