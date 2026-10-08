package org.optipace.adminService.service;

import org.optipace.adminService.dto.response.SingleResponse;

import java.util.Map;

public interface OrganizationCountService {
    
    SingleResponse<Map<String, Object>> getOrganizationCount();
    
    SingleResponse<Long> getOrganizationCountByStatus(String status);
    
    SingleResponse<Long> getOrganizationCountByAdmin(String adminId);
}