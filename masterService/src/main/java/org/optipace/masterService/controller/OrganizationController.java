package org.optipace.masterService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.optipace.masterService.dto.request.OrganizationRequest;
import org.optipace.masterService.dto.request.OrganizationUpdateRequest;
import org.optipace.masterService.dto.response.ListOfOrganizationResponse;
import org.optipace.masterService.dto.response.OrganizationResponse;
import org.optipace.masterService.dto.response.PageResponse;
import org.optipace.masterService.dto.response.SingleResponse;
import org.optipace.masterService.service.OrganizationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/organization")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @GetMapping("/v1/all")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<PageResponse<ListOfOrganizationResponse>>> getAllOrganization(@RequestParam(defaultValue = "0")int page, @RequestParam(defaultValue = "10")int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(organizationService.getAllOrganization(pageable));
    }

    @PostMapping("/v1/create")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<?>> createOrganization(@Valid @RequestBody OrganizationRequest request, @RequestHeader("X-User-Id") String superAdminId) {
        return ResponseEntity.ok(organizationService.createOrganization(request, superAdminId));
    }

    @PatchMapping("/v1/update/{organizationId}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<?>> updateOrganization(@PathVariable Long organizationId, @Valid @RequestBody OrganizationUpdateRequest request, @RequestHeader("X-User-Id") String superAdminId){
        return ResponseEntity.ok(organizationService.updateOrganization(organizationId, request, superAdminId));
    }

    @GetMapping("/v1/{organizationId}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<OrganizationResponse>> getOrganizationById(@PathVariable Long organizationId){
        return ResponseEntity.ok(organizationService.getOrganizationById(organizationId));
    }

    @DeleteMapping("/v1/delete/{organizationId}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<?>> deleteOrganizationById(@PathVariable Long organizationId, @RequestHeader("X-User-Id") String superAdminId){
        return ResponseEntity.ok(organizationService.deleteOrganizationById(organizationId, superAdminId));
    }
}
