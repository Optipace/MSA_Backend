package org.optipace.adminService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.optipace.adminService.dto.request.PackageRequest;
import org.optipace.adminService.dto.response.PageResponse;
import org.optipace.adminService.dto.response.PackageResponse;
import org.optipace.adminService.dto.response.SingleResponse;
import org.optipace.adminService.service.PackageService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
//@RequestMapping("/api/v1/packages")
@RequestMapping("packages")
@RequiredArgsConstructor
public class PackageController {
    
    private final PackageService packageService;
    
    // ==================== CREATE ====================
    
    @PostMapping("/v1/add")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<PackageResponse>> createPackage(
            @Valid @RequestBody PackageRequest request,
            @RequestHeader("X-User-Id") String adminId) {
        
        log.info("REST request to create package: {}", request.getPackageName());
        
        return new ResponseEntity<>(
            packageService.createPackage(request, adminId),
            HttpStatus.CREATED
        );
    }
    
    // ==================== READ ====================
    @GetMapping("/v1/{id}")
    public ResponseEntity<SingleResponse<PackageResponse>> getPackageById(
            @PathVariable Long id) {
        return ResponseEntity.ok(packageService.getPackageById(id));
    }
    
    @GetMapping("/v1/name/{name}")
    public ResponseEntity<SingleResponse<PackageResponse>> getPackageByName(
            @PathVariable String name) {
        return ResponseEntity.ok(packageService.getPackageByName(name));
    }
    
    @GetMapping("/v1/all")
    public ResponseEntity<SingleResponse<PageResponse<PackageResponse>>> getAllPackages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "packageId") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        
        Pageable pageable = PageRequest.of(page, size,
            Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        
        return ResponseEntity.ok(packageService.getAllPackages(pageable));
    }
    
    @GetMapping("/v1/search")
    public ResponseEntity<SingleResponse<PageResponse<PackageResponse>>> searchPackages(
            @RequestParam String packageName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size,
            Sort.by(Sort.Direction.ASC, "packageName"));
        
        return ResponseEntity.ok(packageService.searchPackages(packageName, pageable));
    }
    
    // ==================== UPDATE ====================
    @PutMapping("/v1/{id}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<PackageResponse>> updatePackage(
            @PathVariable Long id,
            @Valid @RequestBody PackageRequest request,
            @RequestHeader("X-User-Id") String adminId) {
        
        log.info("REST request to update package ID: {}", id);
        
        return ResponseEntity.ok(packageService.updatePackage(id, request, adminId));
    }
    
    // ==================== DELETE — hard ====================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<Void>> deletePackage(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String adminId) {
        
        log.info("REST request to delete package ID: {}", id);
        return ResponseEntity.ok(packageService.deletePackage(id, adminId));
    }
    
    // ==================== DELETE — soft ====================
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<Void>> deactivatePackage(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String adminId) {
        
        log.info("REST request to deactivate package ID: {}", id);
        return ResponseEntity.ok(packageService.softDeletePackage(id, adminId));
    }
    
 // ==================== COUNT ====================
    @GetMapping("/count")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<Map<String, Object>>> getPackageCount(
            @RequestParam(required = false) String status) {
        
        log.info("REST request to count packages — status: {}", status);
        return ResponseEntity.ok(packageService.getPackageCount(status));
    }
    
}