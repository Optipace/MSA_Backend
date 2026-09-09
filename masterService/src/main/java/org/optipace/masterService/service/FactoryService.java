package org.optipace.masterService.service;

import org.optipace.masterService.dto.request.FactoryRequest;
import org.optipace.masterService.dto.request.FactoryUpdateRequest;
import org.optipace.masterService.dto.response.ListOfFactoriesResponse;
import org.optipace.masterService.dto.response.FactoryResponse;
import org.optipace.masterService.dto.response.PageResponse;
import org.optipace.masterService.dto.response.SingleResponse;
import org.springframework.data.domain.Pageable;

public interface FactoryService {
    SingleResponse<PageResponse<ListOfFactoriesResponse>> getAllFactories(Pageable pageable);

    SingleResponse<?> createFactory(FactoryRequest request, String factoryAdminId);

    SingleResponse<?> updateFactory(Long factoryId, FactoryUpdateRequest request, String factoryAdminId);

    SingleResponse<FactoryResponse> getFactoryById(Long factoryId);

    SingleResponse<?> deleteFactoryById(Long factoryId, String factoryAdminId);
}
