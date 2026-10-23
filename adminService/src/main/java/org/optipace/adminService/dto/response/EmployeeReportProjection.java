package org.optipace.adminService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class EmployeeReportProjection {

    private String employeeId;
    private String employeeName;
    private String department;
    private String factory;
    private Long completedAssessments;
    private Long totalAssessments;
    private BigDecimal overallScore;
    private String result;
}