
package org.optipace.garmentService.service;

import java.util.List;

import org.optipace.garmentService.dto.request.AddGarmentBatchRequest;
import org.optipace.garmentService.dto.request.UpdateGarmentBatchRequest;
import org.optipace.garmentService.dto.response.GarmentBatchResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.springframework.data.domain.Pageable;

public interface GarmentBatchService {

    SingleResponse<GarmentBatchResponse> getGarmentBatchById(
            Long garmentBatchId
    );

    SingleResponse<PageResponse<GarmentBatchResponse>> getAllGarmentBatches(
            Pageable pageable
    );

    SingleResponse<?> createGarmentBatch(
            AddGarmentBatchRequest request,
            String adminId
    );

    SingleResponse<?> updateGarmentBatch(
            Long garmentBatchId,
            UpdateGarmentBatchRequest request,
            String adminId
    );
    
    SingleResponse<List<GarmentBatchResponse>> getGarmentsByBatchId(
            Long batchId);}
