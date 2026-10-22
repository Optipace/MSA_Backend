package org.optipace.adminService.controller;

import org.optipace.adminService.repository.NotificationRepository;
import org.optipace.adminService.scheduler.PointsExpiryScheduler;
import org.optipace.adminService.service.PointsService;
import org.optipace.adminService.service.PurchaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.optipace.adminService.dto.response.BillingHistoryResponse;
import org.optipace.adminService.dto.response.SingleResponse;

@RestController
@RequestMapping("/organization/api/v1/")
@RequiredArgsConstructor
public class PointsController {
    
    private final PointsService pointsService;
    private final NotificationRepository notificationRepository;
    private final PointsExpiryScheduler scheduler;
    private final PurchaseService purchaseService; 
    
    @GetMapping("/{orgId}/available")
    public ResponseEntity<?> getAvailable(@PathVariable Long orgId) {
        return ResponseEntity.ok(pointsService.getAvailablePoints(orgId));
    }
    
    @GetMapping("/{orgId}/notifications")
    public ResponseEntity<?> getNotifications(@PathVariable Long orgId) {
        return ResponseEntity.ok(
            notificationRepository.findByOrganizationIdAndIsReadFalseOrderByCreatedOnDesc(orgId));
    }
    
    @PatchMapping("/{orgId}/notifications/read-all")
    public ResponseEntity<?> markAllRead(@PathVariable Long orgId) {
        var list = notificationRepository
                .findByOrganizationIdAndIsReadFalseOrderByCreatedOnDesc(orgId);
        list.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(list);
        return ResponseEntity.ok("Marked " + list.size() + " as read");
    }
    
    @GetMapping("/billing-history/{organizationId}")
    public ResponseEntity<SingleResponse<List<BillingHistoryResponse>>> getBillingHistory(
            @PathVariable Long organizationId) {
        return ResponseEntity.ok(purchaseService.getBillingHistory(organizationId));
    }
    
    // Manual trigger for testing
    @PostMapping("/admin/expire-points/run-now")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<?> runExpiryNow() {
        scheduler.expireOldPoints();
        return ResponseEntity.ok("Expiry job triggered");
    }
}