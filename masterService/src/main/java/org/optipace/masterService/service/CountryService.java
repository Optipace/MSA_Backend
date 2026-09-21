package org.optipace.masterService.service;

import org.optipace.masterService.dto.request.CountryAddRequest;
import org.optipace.masterService.dto.request.CountryUpdateRequest;
import org.optipace.masterService.dto.request.DepartmentUpdateRequest;
import org.optipace.masterService.dto.response.CountryResponse;
import org.optipace.masterService.dto.response.DepartmentResponse;
import org.optipace.masterService.dto.response.PageResponse;
import org.optipace.masterService.dto.response.SingleResponse;
import org.springframework.data.domain.Pageable;

public interface CountryService {
	
   // private final CountryService countryService;

	
    SingleResponse<?> createCountry(CountryAddRequest request, String adminId);
    
   // SingleResponse<?> CountryDepartment(Long id, CountryUpdateRequest request, String adminId);

    SingleResponse<CountryResponse> getCountryById(Long id);

    SingleResponse<PageResponse<CountryResponse>> getAllCountrty(Pageable pageable);

    SingleResponse<?> deleteCountry(Long id, String adminId);

	SingleResponse<?> updateCountry(Long id, CountryUpdateRequest request, String adminId);

}
