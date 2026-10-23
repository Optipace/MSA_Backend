package org.optipace.adminService.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class PerformanceSummaryResponse {

    private BigDecimal averageScore;
    private int completedAssessments;
    private int totalAssessments;
    private int passedAssessments;
    private int failedAssessments;
}