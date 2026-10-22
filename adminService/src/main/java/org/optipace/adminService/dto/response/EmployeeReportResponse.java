package org.optipace.adminService.dto.response;
import lombok.Data;

import java.util.List;

@Data
public class EmployeeReportResponse {

    private List<EmployeeReportItem> employees;
    private int totalEmployees;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}