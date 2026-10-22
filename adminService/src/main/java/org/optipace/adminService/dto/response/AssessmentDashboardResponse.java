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
public class AssessmentDashboardResponse {
    private PerformanceSummaryResponse performanceSummary;
    private List<ScoreDistributionResponse> scoreDistribution;
    private List<EmployeeAssessmentResponse> employeeResults;
}
