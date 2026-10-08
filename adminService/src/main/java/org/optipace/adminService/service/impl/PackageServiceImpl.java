package org.optipace.adminService.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.optipace.adminService.dto.request.PackageRequest;
import org.optipace.adminService.dto.response.PageResponse;
import org.optipace.adminService.dto.response.PackageResponse;
import org.optipace.adminService.dto.response.SingleResponse;
import org.optipace.adminService.entity.SubscriptionPackage;
import org.optipace.adminService.enums.CustomStatus;
import org.optipace.adminService.exception.BadRequestException;
import org.optipace.adminService.exception.ResourceNotFoundException;
import org.optipace.adminService.repository.PackageRepository;
import org.optipace.adminService.service.PackageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PackageServiceImpl implements PackageService {
    
    private final PackageRepository packageRepository;
    private final ModelMapper modelMapper;
    // ==================== CREATE ====================
    @Override
    @Transactional
    public SingleResponse createPackage(PackageRequest request, String adminId) {
        log.info("Creating package: {}", request.getPackageName());
        
        if (packageRepository.existsByPackageName(request.getPackageName())) {
            throw new BadRequestException(
                "Package '" + request.getPackageName() + "' already exists");
        }
        
        SubscriptionPackage subscription = new SubscriptionPackage();
        
        subscription.setPackageName(request.getPackageName());
        subscription.setUsageDescription(request.getUsageDescription());
        subscription.setPrice(request.getPrice());
        subscription.setCurrency(request.getCurrency() != null ? request.getCurrency() : "INR");
        subscription.setNumberOfDays(request.getNumberOfDays());
        subscription.setUsage(request.getUsage());
        subscription.setRecordStatus(
            request.getRecordStatus() != null && !request.getRecordStatus().isBlank()
                ? request.getRecordStatus().charAt(0)
                : 'A'
        );
        subscription.setCreatedBy(Long.parseLong(adminId));
        
       // SubscriptionPackage saved = packageRepository.saveAndFlush(subscription);
        SubscriptionPackage saved = packageRepository.save(subscription);
        log.info("Package created: ID={}", saved.getPackageId());
        
//        return SingleResponse.success(
//            toResponse(saved),
//            "PACKAGE CREATED SUCCESSFULLY"
//        );
//        
        return SingleResponse.success("Package created successfully"
                + (saved.getPackageName() != null ? " with name: " + saved.getPackageName() : ""));

    }
    
    // ==================== READ ====================
    @Override
    @Transactional(readOnly = true)
    public SingleResponse<PackageResponse> getPackageById(Long id) {
        SubscriptionPackage entity = packageRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Package", "packageId", id));
        return SingleResponse.success(toResponse(entity));
    }
    
    @Override
    @Transactional(readOnly = true)
    public SingleResponse<PackageResponse> getPackageByName(String name) {
        // Uses JPQL — safe with reserved word
        SubscriptionPackage entity = packageRepository.findAll().stream()
            .filter(p -> p.getPackageName().equalsIgnoreCase(name))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException(
                "Package", "packageName", name));
        return SingleResponse.success(toResponse(entity));
    }
    
    @Override
    @Transactional(readOnly = true)
    public SingleResponse<PageResponse<PackageResponse>> getAllPackages(Pageable pageable) {
        Page<PackageResponse> page = packageRepository
            .findAll(pageable)
            .map(this::toResponse);
        return SingleResponse.success(PageResponse.of(page));
    }
    
    @Override
    @Transactional(readOnly = true)
    public SingleResponse<PageResponse<PackageResponse>> searchPackages(String name, Pageable pageable) {
        Page<PackageResponse> page = packageRepository
            .findByPackageNameContainingIgnoreCase(name, pageable)
            .map(this::toResponse);
        return SingleResponse.success(PageResponse.of(page));
    }
    
    // ==================== UPDATE ====================
    @Override
    @Transactional
    public SingleResponse<PackageResponse> updatePackage(Long id, PackageRequest request, String adminId) {
        log.info("Updating package ID: {}", id);
        
        SubscriptionPackage existing = packageRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Package", "packageId", id));
        
        if (packageRepository.existsByPackageNameAndPackageIdNot(request.getPackageName(), id)) {
            throw new BadRequestException(
                "Package name '" + request.getPackageName() + "' is already in use");
        }
        
        existing.setPackageName(request.getPackageName());
        existing.setUsageDescription(request.getUsageDescription());
        existing.setPrice(request.getPrice());
        existing.setCurrency(request.getCurrency() != null ? request.getCurrency() : "INR");
        existing.setNumberOfDays(request.getNumberOfDays());
        existing.setUsage(request.getUsage());
        
        if (request.getRecordStatus() != null && !request.getRecordStatus().isBlank()) {
            existing.setRecordStatus(request.getRecordStatus().charAt(0));
        }
        existing.setUpdatedBy(Long.parseLong(adminId));
        
        SubscriptionPackage updated = packageRepository.saveAndFlush(existing);
        log.info("Package updated: ID={}", updated.getPackageId());
        
        return SingleResponse.success(
            toResponse(updated),
            "PACKAGE UPDATED SUCCESSFULLY"
        );
    }
    
    // ==================== DELETE (hard) ====================
    @Override
    @Transactional
    public SingleResponse<Void> deletePackage(Long id, String adminId) {
        SubscriptionPackage entity = packageRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Package", "packageId", id));
        packageRepository.delete(entity);
        log.info("Package hard-deleted: ID={}", id);
        return SingleResponse.success(null, "PACKAGE DELETED SUCCESSFULLY");
    }
    
    // ==================== SOFT DELETE ====================
    @Override
    @Transactional
    public SingleResponse<Void> softDeletePackage(Long id, String adminId) {
        SubscriptionPackage entity = packageRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Package", "packageId", id));
        entity.setRecordStatus('I');    // I = Inactive
        entity.setUpdatedBy(Long.parseLong(adminId));
        packageRepository.saveAndFlush(entity);
        log.info("Package soft-deleted: ID={}", id);
        return SingleResponse.success(null, "PACKAGE DEACTIVATED SUCCESSFULLY");
    }
    
    // ==================== HELPER ====================
    private PackageResponse toResponse(SubscriptionPackage e) {
        PackageResponse r = new PackageResponse();
        r.setPackageId(e.getPackageId());
        r.setPackageName(e.getPackageName());
        r.setUsageDescription(e.getUsageDescription());
        r.setPrice(e.getPrice());
        r.setCurrency(e.getCurrency());
        r.setNumberOfDays(e.getNumberOfDays());
        r.setUsage(e.getUsage());
        r.setRecordStatus(e.getRecordStatus() != null ? String.valueOf(e.getRecordStatus()) : null);
        r.setCreatedBy(e.getCreatedBy());
        r.setCreatedOn(e.getCreatedOn());
        r.setUpdatedBy(e.getUpdatedBy());
        r.setUpdatedOn(e.getUpdatedOn());
        return r;
    }
    
 // ==================== COUNT ====================
    @Override
    @Transactional(readOnly = true)
    public SingleResponse<Map<String, Object>> getPackageCount(String status) {
        log.info("Counting packages — status filter: {}", status);
        
        Map<String, Object> counts = new LinkedHashMap<>();
        
        // Total count
        long total = packageRepository.countBy();
        counts.put("total", total);
        
        // If status filter is provided
        if (status != null && !status.isBlank()) {
            Character recordStatus = status.trim().toUpperCase().charAt(0);
            
            if (recordStatus != 'A' && recordStatus != 'I') {
                throw new BadRequestException(
                    "Invalid status '" + status + "'. Allowed: A (Active), I (Inactive)"
                );
            }
            
            long filtered = packageRepository.countByRecordStatus(recordStatus);
            counts.put("status", String.valueOf(recordStatus));
            counts.put("count", filtered);
        } else {
            // No filter — return breakdown by status
            counts.put("active",   packageRepository.countByRecordStatus('A'));
            counts.put("inactive", packageRepository.countByRecordStatus('I'));
        }
        
        // ✅ Explicit type — no inference issues
        return new SingleResponse<Map<String, Object>>(counts, CustomStatus.SUCCESS);
    }
    
}