package org.optipace.adminService.controller;

import lombok.RequiredArgsConstructor;
import org.optipace.adminService.dto.response.SingleResponse;
import org.optipace.adminService.service.ReportService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.optipace.adminService.dto.response.OrgAdminCountResponse;
import org.optipace.adminService.dto.response.OrganizationOverviewReportResponse;
import org.optipace.adminService.dto.response.OrganizationPlanReportResponse;

@RestController
@RequestMapping("reports/")
@RequiredArgsConstructor
public class ReportController {

	private final ReportService reportService;

	// ⭐ SUPER ADMIN REPORT ENDPOINT
	@GetMapping("api/v1/assessments")
	// @PreAuthorize("hasAuthority('SUPER_ADMIN')") // Uncomment if using security
	public ResponseEntity<SingleResponse<?>> getAssessmentReport(@RequestParam(required = false) Long organizationId, // Optional
																														// filter
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size);

		return ResponseEntity.ok(reportService.getAssessmentReport(organizationId, pageable));
	}

	@GetMapping("api/v1/admin-count")
	// @PreAuthorize("hasAuthority('SUPER_ADMIN')") // Uncomment if using security
	public ResponseEntity<SingleResponse<List<OrgAdminCountResponse>>> getOrganizationAdminCounts() {
		return ResponseEntity.ok(reportService.getOrganizationAdminCounts());
	}

	@GetMapping("api/v1/plans-donut")
	public ResponseEntity<SingleResponse<OrganizationPlanReportResponse>> getOrganizationPlanReport() {

		// Call the service
		SingleResponse<OrganizationPlanReportResponse> response = reportService.getOrganizationPlanReport();

		// Return it to the frontend
		return ResponseEntity.ok(response);
	}
	
	 @GetMapping("api/v1/organization-overview")
	    public ResponseEntity<SingleResponse<OrganizationOverviewReportResponse>> getOrganizationOverview(
	            @RequestParam Long superAdminId) {
	        
	        // This now matches the interface exactly
	        return ResponseEntity.ok(reportService.getOrganizationOverview(superAdminId));
	    }

}