package org.optipace.adminService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationPlanReportResponse {
	 private Long totalOrganizations;       // e.g., 264 (Center of the donut)
	 private List<PackageChartDataResponse> chartData;
}
