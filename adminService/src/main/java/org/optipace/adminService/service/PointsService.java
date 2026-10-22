package org.optipace.adminService.service;

import org.optipace.adminService.dto.response.SingleResponse;

import java.time.LocalDate;
import java.util.Map;

public interface PointsService {
    
    /** Get total available (non-expired) points + breakdown */
    SingleResponse<Map<String, Object>> getAvailablePoints(Long organizationId);
    
    /** Consume points FIFO from oldest non-expired package */
    void consumePoints(Long organizationId, Long pointsToConsume,
                       String referenceType, Long referenceId, Long userId);
    
    /** Credit points when a package is purchased */
    void creditPoints(Long organizationId, Long organizationPackageId,
                      Long points, LocalDate expiryDate, Long userId);
}