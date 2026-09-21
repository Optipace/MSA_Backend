package org.optipace.garmentService.service;

import org.optipace.garmentService.dto.request.GarmentInstanceCreateRequest;
import org.optipace.garmentService.dto.request.GarmentInstanceUpdateRequest;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.springframework.data.domain.Pageable;

public interface GarmentInstanceService {

    SingleResponse<?> createGarmentInstance(
            GarmentInstanceCreateRequest request,
            String userId
    );

    SingleResponse<?> getGarmentInstance(
            String serialNumber
    );

    SingleResponse<?> getAllGarmentInstances(
            Pageable pageable
    );

    SingleResponse<?> updateGarmentInstance(
            String serialNumber,
            GarmentInstanceUpdateRequest request,
            String userId
    );

    SingleResponse<?> deleteGarmentInstance(
            String serialNumber,
            String userId
    );
}