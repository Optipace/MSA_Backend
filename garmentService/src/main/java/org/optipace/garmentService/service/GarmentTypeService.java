package org.optipace.garmentService.service;

import org.optipace.garmentService.dto.request.AddGarmentTypeRequest;
import org.optipace.garmentService.dto.request.UpdateGarmentTypeRequest;
import org.optipace.garmentService.dto.response.GarmentTypeResponse;
import org.optipace.garmentService.dto.response.ListOfGarmentTypeResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.springframework.data.domain.Pageable;

public interface GarmentTypeService {

    SingleResponse<?> createGarmentType(
            AddGarmentTypeRequest request,
            String adminId
    );

    SingleResponse<PageResponse<ListOfGarmentTypeResponse>> getAllGarmentTypes(
            Pageable pageable
    );

    SingleResponse<GarmentTypeResponse> getGarmentTypeById(
            Long garmentTypeId
    );

    SingleResponse<?> updateGarmentType(
            Long garmentTypeId,
            UpdateGarmentTypeRequest request,
            String adminId
    );

    SingleResponse<?> deleteGarmentTypeById(
            Long garmentTypeId,
            String adminId
    );
}