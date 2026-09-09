package org.optipace.masterService.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.optipace.masterService.dto.request.DepartmentAddRequest;
import org.optipace.masterService.dto.request.DepartmentUpdateRequest;
import org.optipace.masterService.dto.response.DepartmentResponse;
import org.optipace.masterService.dto.response.PageResponse;
import org.optipace.masterService.dto.response.SingleResponse;
import org.optipace.masterService.entity.Department;
import org.optipace.masterService.enums.CustomStatus;
import org.optipace.masterService.exception.BadRequestException;
import org.optipace.masterService.exception.NotFoundException;
import org.optipace.masterService.repository.DepartmentRepository;
import org.optipace.masterService.service.DepartmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;

    @Override
    public SingleResponse<?> createDepartment(DepartmentAddRequest request, String adminId) {
        if (departmentRepository.existsByDepartmentCode(request.getDepartmentCode())) {
            throw new BadRequestException("Department code already exists");
        }

        Department department = new Department();
        department.setDepartmentCode(request.getDepartmentCode());
        department.setDepartmentName(request.getDepartmentName());
        department.setCreatedBy(Long.parseLong(adminId));
        department.setRecordStatus('A');

        departmentRepository.save(department);

        return new SingleResponse<>("Department created successfully", CustomStatus.SUCCESS);
    }

    @Override
    @Transactional
    public SingleResponse<?> updateDepartment(Long id, DepartmentUpdateRequest request, String adminId) {
        Department Department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Department not found"));

        if (request.getDepartmentCode() != null) {
            if (departmentRepository.existsByDepartmentCodeAndDepartmentIdNot(request.getDepartmentCode(), id)) {
                throw new BadRequestException("Department code already in use");
            }
            Department.setDepartmentCode(request.getDepartmentCode());
        }

        if (request.getDepartmentName() != null) Department.setDepartmentName(request.getDepartmentName());
        if (request.getRemarks() != null) Department.setRemarks(request.getRemarks());

        Department.setUpdatedBy(Long.parseLong(adminId));
        departmentRepository.save(Department);

        return new SingleResponse<>("Department updated successfully", CustomStatus.SUCCESS);
    }

    @Override
    public SingleResponse<DepartmentResponse> getDepartmentById(Long id) {
        Department Department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Department not found"));

        DepartmentResponse response = modelMapper.map(Department, DepartmentResponse.class);
        return new SingleResponse<>(response, CustomStatus.SUCCESS);
    }

    @Override
    public SingleResponse<PageResponse<DepartmentResponse>> getAllDepartments(Pageable pageable) {
        Page<Department> DepartmentPage = departmentRepository.findByRecordStatus('A', pageable);

        List<DepartmentResponse> content = DepartmentPage.getContent().stream()
                .map(Department -> modelMapper.map(Department, DepartmentResponse.class))
                .collect(Collectors.toList());

        PageResponse<DepartmentResponse> pageResponse = new PageResponse<>(
                content,
                DepartmentPage.getNumber(),
                DepartmentPage.getSize(),
                DepartmentPage.getTotalElements(),
                DepartmentPage.getTotalPages(),
                DepartmentPage.isLast()
        );

        return new SingleResponse<>(pageResponse, CustomStatus.SUCCESS);
    }

    @Override
    @Transactional
    public SingleResponse<?> deleteDepartment(Long id, String adminId) {
        Department Department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Department not found"));

        Department.setRecordStatus('D');
        Department.setUpdatedBy(Long.parseLong(adminId));
        departmentRepository.save(Department);

        return new SingleResponse<>("Department deleted successfully", CustomStatus.SUCCESS);
    }
}
