package org.optipace.garmentService.service;

import org.optipace.garmentService.dto.request.GarmentAreaDefectRequest;
import org.optipace.garmentService.dto.request.GarmentAreaDefectUpdateRequest;
import org.optipace.garmentService.dto.response.GarmentAreaDefectResponse;
import org.optipace.garmentService.dto.response.ListOfGarmentAreaDefectResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.springframework.data.domain.Pageable;

public interface GarmentAreaDefectService {

    SingleResponse<?> createGarmentAreaDefect(GarmentAreaDefectRequest request, String userId);

    SingleResponse<GarmentAreaDefectResponse> getGarmentAreaDefectById(Long garmentAreaDefectId);

    SingleResponse<PageResponse<ListOfGarmentAreaDefectResponse>> getAllGarmentAreaDefects(Pageable pageable);

    SingleResponse<?> updateGarmentAreaDefect(Long garmentAreaDefectId, GarmentAreaDefectUpdateRequest request, String userId);

    SingleResponse<?> deleteGarmentAreaDefect(Long garmentAreaDefectId, String userId);
}