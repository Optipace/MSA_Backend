package org.optipace.adminService.service;
import java.util.Map;

import org.optipace.adminService.dto.request.PackageRequest;
import org.optipace.adminService.dto.response.PackageResponse;
import org.optipace.adminService.dto.response.PageResponse;
import org.optipace.adminService.dto.response.SingleResponse;
import org.springframework.data.domain.Pageable;

public interface PackageService {
    
    SingleResponse<PackageResponse> createPackage(PackageRequest request, String adminId);
    
    SingleResponse<PackageResponse> getPackageById(Long id);
    
    SingleResponse<PackageResponse> getPackageByName(String name);
    
    SingleResponse<PageResponse<PackageResponse>> getAllPackages(Pageable pageable);
    
    SingleResponse<PageResponse<PackageResponse>> searchPackages(String name, Pageable pageable);
    
    SingleResponse<PackageResponse> updatePackage(Long id, PackageRequest request, String adminId);
    
    SingleResponse<Void> deletePackage(Long id, String adminId);
    
    SingleResponse<Void> softDeletePackage(Long id, String adminId);

 // ⭐ NEW — returns Map with counts
    SingleResponse<Map<String, Object>> getPackageCount(String status);

}