package org.optipace.garmentService.service;

import org.optipace.garmentService.dto.request.GarmentExpectedDefectRequest;
import org.optipace.garmentService.dto.request.GarmentExpectedDefectUpdateRequest;
import org.optipace.garmentService.dto.response.GarmentExpectedDefectResponse;
import org.optipace.garmentService.dto.response.ListOfGarmentExpectedDefectResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GarmentExpectedDefectService {

    SingleResponse<?> createGarmentExpectedDefect(
            GarmentExpectedDefectRequest request,
            String userId
    );

    SingleResponse<GarmentExpectedDefectResponse> getGarmentExpectedDefectById(
            UUID garmentExpectedDefectId
    );

    SingleResponse<?> updateGarmentExpectedDefect(
            UUID garmentExpectedDefectId,
            GarmentExpectedDefectUpdateRequest request
    );

    SingleResponse<PageResponse<ListOfGarmentExpectedDefectResponse>> getAllGarmentExpectedDefects(
            Pageable pageable
    );

}