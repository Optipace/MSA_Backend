package org.optipace.masterService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.optipace.masterService.dto.request.DepartmentAddRequest;
import org.optipace.masterService.dto.request.DepartmentUpdateRequest;
import org.optipace.masterService.dto.response.DepartmentResponse;
import org.optipace.masterService.dto.response.PageResponse;
import org.optipace.masterService.dto.response.SingleResponse;
import org.optipace.masterService.service.DepartmentService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping("/v1/add")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> createDepartment(@Valid @RequestBody DepartmentAddRequest request,
                                                               @RequestHeader("X-User-Id") String adminId) {
        return ResponseEntity.ok(departmentService.createDepartment(request, adminId));
    }

    @PatchMapping("/v1/{id}")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> updateDepartment(@PathVariable Long id,
                                                               @Valid @RequestBody DepartmentUpdateRequest request,
                                                               @RequestHeader("X-User-Id") String adminId) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, request, adminId));
    }

    @GetMapping("/v1/{id}")
//    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<DepartmentResponse>> getDepartmentById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @GetMapping("/v1/all")
//    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<PageResponse<DepartmentResponse>>> getAllDepartments(@RequestParam(defaultValue = "0") int page,
                                                                                                @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(departmentService.getAllDepartments(pageable));
    }

    @DeleteMapping("/v1/{id}")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> deleteDepartment(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String adminId) {
        return ResponseEntity.ok(departmentService.deleteDepartment(id, adminId));
    }
}