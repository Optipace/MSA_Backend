package org.optipace.adminService.service;

import java.util.List;

import org.optipace.adminService.dto.response.OrgAdminCountResponse;
import org.optipace.adminService.dto.response.OrganizationOverviewReportResponse;
import org.optipace.adminService.dto.response.OrganizationOverviewResponse;
import org.optipace.adminService.dto.response.OrganizationPlanReportResponse;
import org.optipace.adminService.dto.response.SingleResponse;
import org.springframework.data.domain.Pageable;

public interface ReportService {
    SingleResponse<?> getAssessmentReport(Long organizationId, Pageable pageable);
    // ⭐ New method
    SingleResponse<List<OrgAdminCountResponse>> getOrganizationAdminCounts();
    SingleResponse<OrganizationPlanReportResponse> getOrganizationPlanReport();
    //SingleResponse<OrganizationOverviewResponse> getOrganizationOverview(Long superAdminId);

    SingleResponse<OrganizationOverviewReportResponse> getOrganizationOverview(Long superAdminId);



}