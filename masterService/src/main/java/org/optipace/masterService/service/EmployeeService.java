package org.optipace.masterService.service;

import org.optipace.masterService.dto.request.AddEmployeeRequest;
import org.optipace.masterService.dto.request.UpdateEmployeeDetailsRequest;
import org.optipace.masterService.dto.response.EmployeeResponse;
import org.optipace.masterService.dto.response.ListOfEmployeeResponse;
import org.optipace.masterService.dto.response.PageResponse;
import org.optipace.masterService.dto.response.SingleResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {

    SingleResponse<?> createEmployee(List<AddEmployeeRequest> requestList, String adminId);

    SingleResponse<PageResponse<ListOfEmployeeResponse>> getAllEmployee(Pageable pageable);

    SingleResponse<?> updateEmployee(Long employeeId, UpdateEmployeeDetailsRequest request, String adminId);

    SingleResponse<EmployeeResponse> getEmployeeById(Long employeeId);

    SingleResponse<?> deleteEmployeeById(Long employeeId, String adminId);

    SingleResponse<EmployeeResponse> getEmployeeDetailsByToken(String employeeId);
}
