package org.optipace.adminService.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class AssessmentResultResponse {

    private String assessmentName;
    private LocalDate date;
    private BigDecimal score;
    private String result;
}