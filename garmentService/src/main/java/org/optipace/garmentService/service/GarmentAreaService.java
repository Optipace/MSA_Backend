package org.optipace.garmentService.service;

import java.util.List;

import org.optipace.garmentService.dto.request.AddGarmentAreaRequest;
import org.optipace.garmentService.dto.request.UpdateGarmentAreaRequest;
import org.optipace.garmentService.dto.response.GarmentAreaResponse;
import org.optipace.garmentService.dto.response.ListOfGarmentAreaResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.springframework.data.domain.Pageable;

public interface GarmentAreaService {

    SingleResponse<?> createGarmentArea(
            AddGarmentAreaRequest request,
            String adminId
    );

    SingleResponse<List<ListOfGarmentAreaResponse>> getAllGarmentAreas();
    
    SingleResponse<GarmentAreaResponse>
            getGarmentAreaById(Long garmentAreaId);

    SingleResponse<?> updateGarmentArea(
            Long garmentAreaId,
            UpdateGarmentAreaRequest request,
            String adminId
    );

//    SingleResponse<?> deleteGarmentAreaById(
//            Long garmentAreaId,
//            String adminId
//    );
}