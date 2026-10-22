package org.optipace.adminService.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.optipace.adminService.dto.response.OrgAdminCountResponse;
import org.optipace.adminService.dto.response.OrganizationOverviewReportResponse;
import org.optipace.adminService.dto.response.OrganizationOverviewResponse;
import org.optipace.adminService.dto.response.OrganizationPlanReportResponse;
import org.optipace.adminService.dto.response.PackageChartDataResponse;
import org.optipace.adminService.dto.request.SuperAdminAssessmentReportDto;
import org.optipace.adminService.dto.response.SingleResponse;
import org.optipace.adminService.enums.CustomStatus;
import org.optipace.adminService.repository.AssessmentUsageRepository;
import org.optipace.adminService.repository.OrganizationPackageRepository;
import org.optipace.adminService.repository.OrganizationRepository;
import org.optipace.adminService.service.ReportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

	private final AssessmentUsageRepository assessmentUsageRepository;

	private final OrganizationRepository organizationRepository;
	private final OrganizationPackageRepository organizationPackageRepository;

	@Override
	public SingleResponse<?> getAssessmentReport(Long organizationId, Pageable pageable) {

		log.info("Fetching Super Admin Assessment Report. OrgId filter: {}", organizationId);

		Page<SuperAdminAssessmentReportDto> page = assessmentUsageRepository
				.getSuperAdminAssessmentReport(organizationId, pageable);

		// Format to match your standard pagination response
		Map<String, Object> data = new LinkedHashMap<>();

		// "Total 14 users" from your UI screenshot maps to totalElements
		data.put("totalUsers", page.getTotalElements());

		data.put("content", page.getContent().stream().map(dto -> {
			// Format score to have 1 decimal place (e.g. 82.1)
			if (dto.getScore() != null) {
				dto.setScore(dto.getScore().setScale(1, java.math.RoundingMode.HALF_UP));
			}
			return dto;
		}).collect(Collectors.toList()));

		data.put("page", page.getNumber());
		data.put("size", page.getSize());
		data.put("totalPages", page.getTotalPages());
		data.put("last", page.isLast());

		return new SingleResponse<>(data, CustomStatus.SUCCESS);
	}

	@Override
	public SingleResponse<List<OrgAdminCountResponse>> getOrganizationAdminCounts() {
		log.info("Fetching Admin Count per Organization");
		List<OrgAdminCountResponse> report = organizationRepository.getOrganizationAdminCounts();
		return new SingleResponse<>(report, CustomStatus.SUCCESS);
	}

	@Override
	public SingleResponse<OrganizationPlanReportResponse> getOrganizationPlanReport() {

		// 1. Call the query you made in Step 2
		List<Object[]> results = organizationPackageRepository.countOrganizationsByPackage();

		// 2. Calculate total for the center of the donut
		long totalOrgs = 0;
		for (Object[] row : results) {
			totalOrgs += ((Number) row[1]).longValue();
		}

		// 3. Map the results to your DTO
		List<PackageChartDataResponse> chartData = new java.util.ArrayList<>();
		for (Object[] row : results) {
			String packageName = (String) row[0];
			long count = ((Number) row[1]).longValue();

			// Calculate percentage
			double percentage = totalOrgs > 0 ? ((double) count / totalOrgs) * 100 : 0.0;
			percentage = Math.round(percentage * 10.0) / 10.0; // round to 1 decimal

			chartData.add(
					PackageChartDataResponse.builder().name(packageName).value(count).percentage(percentage).build());
		}

		// 4. Build final response
		OrganizationPlanReportResponse report = OrganizationPlanReportResponse.builder().totalOrganizations(totalOrgs)
				.chartData(chartData).build();

		return new SingleResponse<>(report, CustomStatus.SUCCESS);
	}

	@Override
	public SingleResponse<OrganizationOverviewReportResponse> getOrganizationOverview(Long superAdminId) {
		log.info("Fetching Organization Overview for Super Admin: {}", superAdminId);

		// Fetch rows
		List<OrganizationOverviewReportResponse.OrganizationRow> rows = organizationRepository
				.getOrganizationOverviewByAdmin(superAdminId);

		// Build response
		OrganizationOverviewReportResponse response = OrganizationOverviewReportResponse.builder()
				.totalOrganizations((long) rows.size()).organizations(rows).build();

		return new SingleResponse<>(response, CustomStatus.SUCCESS);
	}

}