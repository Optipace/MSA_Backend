package org.optipace.masterService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.optipace.masterService.dto.request.AddEmployeeRequest;
import org.optipace.masterService.dto.request.UpdateEmployeeDetailsRequest;
import org.optipace.masterService.dto.response.*;
import org.optipace.masterService.service.EmployeeService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping("/v1/add")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> createEmployee(@Valid @RequestBody List<AddEmployeeRequest> requestList, @RequestHeader("X-User-Id") String adminId) {
        return ResponseEntity.ok(employeeService.createEmployee(requestList, adminId));
    }

    @GetMapping("/v1/all")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<PageResponse<ListOfEmployeeResponse>>> getAllEmployee(@RequestParam(defaultValue = "0")int page, @RequestParam(defaultValue = "10")int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(employeeService.getAllEmployee(pageable));
    }

    @PatchMapping("/v1/update/{employeeId}")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> updateEmployee(@PathVariable Long employeeId, @Valid @RequestBody UpdateEmployeeDetailsRequest request, @RequestHeader("X-User-Id") String adminId){
        return ResponseEntity.ok(employeeService.updateEmployee(employeeId, request, adminId));
    }

    @GetMapping("/v1/{employeeId}")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<EmployeeResponse>> getEmployeeById(@PathVariable Long employeeId){
        return ResponseEntity.ok(employeeService.getEmployeeById(employeeId));
    }

    @DeleteMapping("/v1/delete/{employeeId}")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> deleteEmployeeById(@PathVariable Long employeeId, @RequestHeader("X-User-Id") String adminId){
        return ResponseEntity.ok(employeeService.deleteEmployeeById(employeeId, adminId));
    }

    @GetMapping("/v1/getEmployee")
    public ResponseEntity<SingleResponse<EmployeeResponse>> getEmployeeDetailsByToken(@RequestHeader("X-User-Id") String employeeId){
        return ResponseEntity.ok(employeeService.getEmployeeDetailsByToken(employeeId));
    }
}
