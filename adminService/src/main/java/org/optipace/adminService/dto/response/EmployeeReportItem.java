package org.optipace.adminService.dto.response;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class EmployeeReportItem {
    private String employeeId;
    private String employeeName;
    private String department;
    private String factory;
    private String assessment;
    private BigDecimal overallScore;
    private String result;
}