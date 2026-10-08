package org.optipace.adminService.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.optipace.adminService.dto.response.SingleResponse;
import org.optipace.adminService.enums.CustomStatus;
import org.optipace.adminService.exception.BadRequestException;
import org.optipace.adminService.repository.OrganizationCountRepository;
import org.optipace.adminService.service.OrganizationCountService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationCountServiceImpl implements OrganizationCountService {
    
    private final OrganizationCountRepository organizationCountRepository;
    
    // ==================== SUMMARY COUNT ====================
    @Override
    public SingleResponse<Map<String, Object>> getOrganizationCount() {
        log.info("Fetching organization count summary");
        
        Map<String, Object> result = organizationCountRepository.getOrganizationCountSummary();
        
        return new SingleResponse<Map<String, Object>>(result, CustomStatus.SUCCESS);
    }
    
    // ==================== COUNT BY STATUS ====================
    @Override
    public SingleResponse<Long> getOrganizationCountByStatus(String status) {
        log.info("Fetching organization count by status: {}", status);
        
        if (status == null || status.isBlank()) {
            throw new BadRequestException("Status is required (A / I / D)");
        }
        
        String s = status.trim().toUpperCase();
        if (!s.equals("A") && !s.equals("I") && !s.equals("D")) {
            throw new BadRequestException(
                "Invalid status '" + status + "'. Allowed: A, I, D");
        }
        
        long count = organizationCountRepository.countByStatus(s);
        
        return new SingleResponse<Long>(count, CustomStatus.SUCCESS);
    }
    
    // ==================== COUNT BY ADMIN ====================
    @Override
    public SingleResponse<Long> getOrganizationCountByAdmin(String adminId) {
        log.info("Fetching organization count for admin: {}", adminId);
        
        Long adminIdLong;
        try {
            adminIdLong = Long.parseLong(adminId);
        } catch (NumberFormatException ex) {
            throw new BadRequestException("Invalid adminId header: " + adminId);
        }
        
        long count = organizationCountRepository.countByCreatedBy(adminIdLong);
        
        return new SingleResponse<Long>(count, CustomStatus.SUCCESS);
    }
}