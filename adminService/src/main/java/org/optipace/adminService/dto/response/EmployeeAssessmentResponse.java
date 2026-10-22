package org.optipace.adminService.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeAssessmentResponse {
    private String employeeName;
    private String employeeId;
    private String department;
    private double score;
    private String result;
}
