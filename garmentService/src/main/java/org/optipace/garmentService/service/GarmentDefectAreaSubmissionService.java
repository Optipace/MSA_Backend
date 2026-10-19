package org.optipace.garmentService.service;

import java.util.List;

import org.optipace.garmentService.dto.request.GarmentDefectAreaSubmissionRequest;
import org.optipace.garmentService.dto.response.GarmentDefectAreaSubmissionResponse;
import org.optipace.garmentService.dto.response.SingleResponse;

public interface GarmentDefectAreaSubmissionService {

    SingleResponse<List<GarmentDefectAreaSubmissionResponse>> submit(
            GarmentDefectAreaSubmissionRequest request,
            String userId);

    SingleResponse<List<GarmentDefectAreaSubmissionResponse>> getByBatchId(
            Long batchId);

    SingleResponse<List<GarmentDefectAreaSubmissionResponse>>
            getByBatchIdAndGarmentId(Long batchId, String garmentId);
}