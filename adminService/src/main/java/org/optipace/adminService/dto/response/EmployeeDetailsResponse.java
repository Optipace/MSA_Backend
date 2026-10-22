package org.optipace.adminService.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class EmployeeDetailsResponse {

    private String employeeId;
    private String employeeName;
    private String department;
    private String factory;

    private PerformanceSummaryResponse performanceSummary;

    private List<AssessmentPerformanceResponse> assessmentPerformance;

    private List<AssessmentResultResponse> assessmentResults;
}