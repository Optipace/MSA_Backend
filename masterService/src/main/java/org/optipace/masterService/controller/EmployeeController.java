package org.optipace.masterService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.apache.http.HttpStatus;
import org.optipace.masterService.dto.request.AddAdminRequest;
import org.optipace.masterService.dto.request.AddEmployeeRequest;
import org.optipace.masterService.dto.request.AddSingleEmployeeRequest;
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

	@PostMapping("/v1/add-admin")
	// @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')") // uncomment
	// in production
	public ResponseEntity<SingleResponse<?>> addAdmin(@Valid @RequestBody AddAdminRequest request,
			@RequestHeader("X-User-Id") String adminId) {

		// log.info("REST request to add admin: {}", request.getAdminId());

		return ResponseEntity.ok(employeeService.addAdmin(request, adminId));
	}

	@PutMapping("/v1/update-admin/{employeeId}")
	// @PreAuthorize("hasAuthority('SUPER_ADMIN')") // Uncomment if using security
	public ResponseEntity<SingleResponse<?>> updateAdmin(@PathVariable Long employeeId,
			@Valid @RequestBody AddAdminRequest request) {

		// log.info("REST request to update admin: {}", employeeId);

		return ResponseEntity.ok(employeeService.updateAdmin(employeeId, request));
	}

	@PostMapping("/v1/add-single")
	@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
	public ResponseEntity<SingleResponse<?>> addSingleEmployee(@Valid @RequestBody AddSingleEmployeeRequest request,
			@RequestHeader("X-User-Id") String adminId) {

		// log.info("REST request to add single employee: {}",
		// request.getEmployeeCode());

		// return new ResponseEntity<>(employeeService.addSingleEmployee(request,
		// adminId), HttpStatus.CREATED);
		return ResponseEntity.ok(employeeService.addSingleEmployee(request, adminId));

	}

	@PostMapping("/v1/add")
	@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
	public ResponseEntity<SingleResponse<?>> createEmployee(@Valid @RequestBody List<AddEmployeeRequest> requestList,
			@RequestHeader("X-User-Id") String adminId) {
		return ResponseEntity.ok(employeeService.createEmployee(requestList, adminId));
	}

	@GetMapping("/v1/all")
	@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
	public ResponseEntity<SingleResponse<PageResponse<ListOfEmployeeResponse>>> getAllEmployee(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(employeeService.getAllEmployee(pageable));
	}

	@PatchMapping("/v1/update/{employeeId}")
	@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
	public ResponseEntity<SingleResponse<?>> updateEmployee(@PathVariable Long employeeId,
			@Valid @RequestBody UpdateEmployeeDetailsRequest request, @RequestHeader("X-User-Id") String adminId) {
		return ResponseEntity.ok(employeeService.updateEmployee(employeeId, request, adminId));
	}

	@GetMapping("/v1/{employeeId}")
	@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
	public ResponseEntity<SingleResponse<EmployeeResponse>> getEmployeeById(@PathVariable Long employeeId) {
		return ResponseEntity.ok(employeeService.getEmployeeById(employeeId));
	}

	@DeleteMapping("/v1/delete/{employeeId}")
	@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
	public ResponseEntity<SingleResponse<?>> deleteEmployeeById(@PathVariable Long employeeId,
			@RequestHeader("X-User-Id") String adminId) {
		return ResponseEntity.ok(employeeService.deleteEmployeeById(employeeId, adminId));
	}

	@GetMapping("/v1/getEmployee")
	@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
	public ResponseEntity<SingleResponse<EmployeeResponse>> getEmployeeDetailsByToken(
			@RequestHeader("X-User-Id") String employeeId) {
		return ResponseEntity.ok(employeeService.getEmployeeDetailsByToken(employeeId));
	}
}
