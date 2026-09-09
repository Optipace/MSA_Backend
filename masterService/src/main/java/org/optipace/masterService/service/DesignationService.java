package org.optipace.masterService.service;

import org.optipace.masterService.dto.request.DesignationRequest;
import org.optipace.masterService.dto.request.DesignationUpdateRequest;
import org.optipace.masterService.dto.response.DesignationResponse;
import org.optipace.masterService.dto.response.PageResponse;
import org.optipace.masterService.dto.response.SingleResponse;
import org.springframework.data.domain.Pageable;

public interface DesignationService {
    SingleResponse<?> createDesignation(DesignationRequest request, String adminId);

    SingleResponse<?> updateDesignation(Long id, DesignationUpdateRequest request, String adminId);

    SingleResponse<DesignationResponse> getDesignationById(Long id);

    SingleResponse<PageResponse<DesignationResponse>> getAllDesignations(Pageable pageable);

    SingleResponse<?> deleteDesignation(Long id, String adminId);
}
