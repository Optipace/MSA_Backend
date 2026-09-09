package org.optipace.masterService.service;

import org.optipace.masterService.dto.request.DepartmentAddRequest;
import org.optipace.masterService.dto.request.DepartmentUpdateRequest;
import org.optipace.masterService.dto.response.DepartmentResponse;
import org.optipace.masterService.dto.response.PageResponse;
import org.optipace.masterService.dto.response.SingleResponse;
import org.springframework.data.domain.Pageable;

public interface DepartmentService {
    SingleResponse<?> createDepartment(DepartmentAddRequest request, String adminId);

    SingleResponse<?> updateDepartment(Long id, DepartmentUpdateRequest request, String adminId);

    SingleResponse<DepartmentResponse> getDepartmentById(Long id);

    SingleResponse<PageResponse<DepartmentResponse>> getAllDepartments(Pageable pageable);

    SingleResponse<?> deleteDepartment(Long id, String adminId);
}
