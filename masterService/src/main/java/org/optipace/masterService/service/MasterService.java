package org.optipace.masterService.service;

import org.optipace.masterService.dto.response.*;
import org.springframework.data.domain.Pageable;

public interface MasterService {
    SingleResponse<PageResponse<MasterDepartmentResponse>> getAllDepartments(Pageable pageable);

    SingleResponse<PageResponse<MasterDesignationResponse>> getAllDesignations(Pageable pageable);

    SingleResponse<PageResponse<MasterOrganizationResponse>> getAllOrganizations(Pageable pageable);

    SingleResponse<PageResponse<MasterSectionResponse>> getAllSections(Pageable pageable);

    SingleResponse<PageResponse<MasterShiftResponse>> getAllShifts(Pageable pageable);

    SingleResponse<PageResponse<MasterCountryResponse>> getAllCountries(Pageable pageable);

    SingleResponse<PageResponse<MasterStateResponse>> getAllStates(Pageable pageable);

    SingleResponse<PageResponse<MasterCityResponse>> getAllCities(Pageable pageable);

    SingleResponse<PageResponse<MasterFactoryResponse>> getAllFactories(Pageable pageable);
}
