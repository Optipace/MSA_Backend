package org.optipace.masterService.controller;

import org.optipace.masterService.dto.request.CountryAddRequest;
import org.optipace.masterService.dto.request.CountryUpdateRequest;
import org.optipace.masterService.dto.request.DepartmentAddRequest;
import org.optipace.masterService.dto.request.DesignationUpdateRequest;
import org.optipace.masterService.dto.response.CountryResponse;
import org.optipace.masterService.dto.response.DepartmentResponse;
import org.optipace.masterService.dto.response.PageResponse;
import org.optipace.masterService.dto.response.SingleResponse;
import org.optipace.masterService.service.CountryService;
import org.optipace.masterService.service.DepartmentService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/country")
@RequiredArgsConstructor
public class CountryController {

	private final CountryService countryService;

	@PostMapping("/v1/add")
	@PreAuthorize("hasAuthority('SUPER_ADMIN')")
	public ResponseEntity<SingleResponse<?>> createDepartment(@Valid @RequestBody CountryAddRequest request,
			@RequestHeader("X-User-Id") String adminId) {
		return ResponseEntity.ok(countryService.createCountry(request, adminId));
	}

	@PatchMapping("/v1/{id}")
	@PreAuthorize("hasAuthority('SUPER_ADMIN')")
	public ResponseEntity<SingleResponse<?>> updateCountry(@PathVariable Long id,
			@Valid @RequestBody CountryUpdateRequest request, @RequestHeader("X-User-Id") String adminId) {
		return ResponseEntity.ok(countryService.updateCountry(id, request, adminId));
	}

	@GetMapping("/v1/{id}")
//@PreAuthorize("hasAuthority('FACTORY_ADMIN')")
	public ResponseEntity<SingleResponse<CountryResponse>> getCountryById(@PathVariable Long id) {
		return ResponseEntity.ok(countryService.getCountryById(id));
	}

	@GetMapping("/v1/all")
//	    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
	public ResponseEntity<SingleResponse<PageResponse<CountryResponse>>> getAllCountrty(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(countryService.getAllCountrty(pageable));
	}

	@DeleteMapping("/v1/{id}")
	@PreAuthorize("hasAuthority('FACTORY_ADMIN')")
	public ResponseEntity<SingleResponse<?>> deleteCountry(@PathVariable Long id,
			@RequestHeader("X-User-Id") String adminId) {
		return ResponseEntity.ok(countryService.deleteCountry(id, adminId));
	}

}
