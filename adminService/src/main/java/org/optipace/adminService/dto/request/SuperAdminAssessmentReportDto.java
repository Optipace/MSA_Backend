package org.optipace.adminService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuperAdminAssessmentReportDto {
    private String employeeId;       // e.g., EMP0011 (employee_code)
    private String employeeName;     // e.g., Anitha Sharma
    private String department;       // e.g., Production
    private BigDecimal score;        // e.g., 82.1
    private String status;           // e.g., Completed
    private LocalDate date;          // e.g., 20-04-2026
}