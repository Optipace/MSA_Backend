package org.optipace.adminService.service;

import java.util.List;

import org.optipace.adminService.dto.request.UpdateAdminStatusRequest;
import org.optipace.adminService.dto.response.AdminListResponse;
import org.optipace.adminService.dto.response.OrgAdminCountResponse;
import org.optipace.adminService.dto.response.OrganizationOverviewReportResponse;
import org.optipace.adminService.dto.response.OrganizationOverviewResponse;
import org.optipace.adminService.dto.response.OrganizationPlanReportResponse;
import org.optipace.adminService.dto.response.OrganizationUsageResponse;
import org.optipace.adminService.dto.response.PageResponse;
import org.optipace.adminService.dto.response.SingleResponse;
import org.springframework.data.domain.Pageable;

public interface ReportService {
    SingleResponse<?> getAssessmentReport(Long organizationId, Pageable pageable);
    // ⭐ New method
    SingleResponse<List<OrgAdminCountResponse>> getOrganizationAdminCounts();
    SingleResponse<OrganizationPlanReportResponse> getOrganizationPlanReport();
    //SingleResponse<OrganizationOverviewResponse> getOrganizationOverview(Long superAdminId);

    SingleResponse<OrganizationOverviewReportResponse> getOrganizationOverview(Long superAdminId);
    
    // Get Total Admin count
    SingleResponse<Long> getTotalAdminCount(Long userAdminId);
    
    SingleResponse<Long> getTotalPackageCount();

   // SingleResponse<List<AdminListResponse>> getAllAdminsBySuperAdmin(Long superAdminId);
    
 // ✅ ADD THIS NEW LINE:
    SingleResponse<PageResponse<AdminListResponse>> getAllAdminsBySuperAdmin(
            Long superAdminId, 
            Pageable pageable
    );
    
    SingleResponse<?> updateAdminStatus(Long employeeId, UpdateAdminStatusRequest request, String adminId);

    SingleResponse<PageResponse<OrganizationUsageResponse>> getOrganizationUsage(
            String search, 
            Pageable pageable
    );
}