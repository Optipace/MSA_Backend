
package org.optipace.adminService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentAssessmentResponse {

    private String employeeId;
    private String employeeName;
    private String department;
    private String assessment;
    private BigDecimal score;
    private String status;
    private String date;
}