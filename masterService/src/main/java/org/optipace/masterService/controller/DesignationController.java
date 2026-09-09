package org.optipace.masterService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.optipace.masterService.dto.request.DesignationRequest;
import org.optipace.masterService.dto.request.DesignationUpdateRequest;
import org.optipace.masterService.dto.response.DesignationResponse;
import org.optipace.masterService.dto.response.PageResponse;
import org.optipace.masterService.dto.response.SingleResponse;
import org.optipace.masterService.service.DesignationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/designation")
@RequiredArgsConstructor
public class DesignationController {

    private final DesignationService designationService;

    @PostMapping("/v1/add")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> createDesignation(@Valid @RequestBody DesignationRequest request,
                                                               @RequestHeader("X-User-Id") String adminId) {
        return ResponseEntity.ok(designationService.createDesignation(request, adminId));
    }

    @PatchMapping("/v1/{id}")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> updateDesignation(@PathVariable Long id,
                                                               @Valid @RequestBody DesignationUpdateRequest request,
                                                               @RequestHeader("X-User-Id") String adminId) {
        return ResponseEntity.ok(designationService.updateDesignation(id, request, adminId));
    }

    @GetMapping("/v1/{id}")
//    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<DesignationResponse>> getDesignationById(@PathVariable Long id) {
        return ResponseEntity.ok(designationService.getDesignationById(id));
    }

    @GetMapping("/v1/all")
//    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<PageResponse<DesignationResponse>>> getAllDesignations(@RequestParam(defaultValue = "0") int page,
                                                                                                @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(designationService.getAllDesignations(pageable));
    }

    @DeleteMapping("/v1/{id}")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> deleteDesignation(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String adminId) {
        return ResponseEntity.ok(designationService.deleteDesignation(id, adminId));
    }
}
