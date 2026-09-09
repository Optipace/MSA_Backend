package org.optipace.masterService.service;

import org.optipace.masterService.dto.request.OrganizationRequest;
import org.optipace.masterService.dto.request.OrganizationUpdateRequest;
import org.optipace.masterService.dto.response.ListOfOrganizationResponse;
import org.optipace.masterService.dto.response.OrganizationResponse;
import org.optipace.masterService.dto.response.PageResponse;
import org.optipace.masterService.dto.response.SingleResponse;
import org.springframework.data.domain.Pageable;

public interface OrganizationService {

    SingleResponse<PageResponse<ListOfOrganizationResponse>> getAllOrganization(Pageable pageable);

    SingleResponse<?> createOrganization(OrganizationRequest request, String superAdminId);

    SingleResponse<?> updateOrganization(Long organizationId, OrganizationUpdateRequest request, String superAdminId);

    SingleResponse<OrganizationResponse> getOrganizationById(Long organizationId);

    SingleResponse<?> deleteOrganizationById(Long organizationId, String superAdminId);
}
