package org.optipace.adminService.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class AssessmentPerformanceResponse {

    private String moduleCode;
    private String assessmentName;
    private BigDecimal score;
}