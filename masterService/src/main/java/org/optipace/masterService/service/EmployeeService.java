package org.optipace.masterService.service;

import jakarta.validation.Valid;
import org.optipace.masterService.dto.request.AddEmployeeRequest;
import org.optipace.masterService.dto.request.UpdateEmployeeDetailsRequest;
import org.optipace.masterService.dto.response.EmployeeResponse;
import org.optipace.masterService.dto.response.ListOfEmployeeResponse;
import org.optipace.masterService.dto.response.PageResponse;
import org.optipace.masterService.dto.response.SingleResponse;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {

    SingleResponse<?> createEmployee(AddEmployeeRequest request, String adminId);

    SingleResponse<PageResponse<ListOfEmployeeResponse>> getAllEmployee(Pageable pageable);

    SingleResponse<?> updateEmployee(Long employeeId, UpdateEmployeeDetailsRequest request, String adminId);

    SingleResponse<EmployeeResponse> getEmployeeById(Long employeeId);

    SingleResponse<?> deleteEmployeeById(Long employeeId, String adminId);

    SingleResponse<EmployeeResponse> getEmployeeDetailsByToken(String employeeId);
}
