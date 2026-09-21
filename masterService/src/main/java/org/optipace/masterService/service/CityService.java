package org.optipace.masterService.service;

import org.optipace.masterService.dto.request.AddCityRequest;
import org.optipace.masterService.dto.request.UpdateCityRequest;
import org.optipace.masterService.dto.response.CityResponse;
import org.optipace.masterService.dto.response.ListOfCityResponse;
import org.optipace.masterService.dto.response.PageResponse;
import org.optipace.masterService.dto.response.SingleResponse;
import org.springframework.data.domain.Pageable;

public interface CityService {

    SingleResponse<?> createCity(AddCityRequest request, String adminId);

    SingleResponse<PageResponse<ListOfCityResponse>> getAllCity(Pageable pageable);

    SingleResponse<?> updateCity(Long cityId, UpdateCityRequest request, String adminId);

    SingleResponse<CityResponse> getCityById(Long cityId);

    SingleResponse<?> deleteCityById(Long cityId, String adminId);
}


