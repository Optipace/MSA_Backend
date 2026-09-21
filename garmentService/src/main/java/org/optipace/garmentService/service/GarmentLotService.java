package org.optipace.garmentService.service;

import org.optipace.garmentService.dto.request.GarmentLotRequest;
import org.optipace.garmentService.dto.request.GarmentLotUpdateRequest;
import org.optipace.garmentService.dto.response.GarmentLotResponse;
import org.optipace.garmentService.dto.response.ListOfGarmentLotResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

public interface GarmentLotService {

    SingleResponse<PageResponse<ListOfGarmentLotResponse>> getAllGarmentLots(Pageable pageable);

    SingleResponse<?> createGarmentLot(GarmentLotRequest request, String userId);

    SingleResponse<?> updateGarmentLot(String lotNumber, GarmentLotUpdateRequest request, String userId);

    SingleResponse<GarmentLotResponse> getGarmentLotByLotNumber(String lotNumber);

    SingleResponse<?> deleteGarmentLotByLotNumber(String lotNumber, String userId);
}