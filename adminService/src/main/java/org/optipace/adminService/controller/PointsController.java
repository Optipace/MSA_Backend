package org.optipace.adminService.controller;

import org.optipace.adminService.repository.NotificationRepository;
import org.optipace.adminService.scheduler.PointsExpiryScheduler;
import org.optipace.adminService.service.PointsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/points")
@RequiredArgsConstructor
public class PointsController {
    
    private final PointsService pointsService;
    private final NotificationRepository notificationRepository;
    private final PointsExpiryScheduler scheduler;
    
    @GetMapping("/organization/{orgId}/available")
    public ResponseEntity<?> getAvailable(@PathVariable Long orgId) {
        return ResponseEntity.ok(pointsService.getAvailablePoints(orgId));
    }
    
    @GetMapping("/organization/{orgId}/notifications")
    public ResponseEntity<?> getNotifications(@PathVariable Long orgId) {
        return ResponseEntity.ok(
            notificationRepository.findByOrganizationIdAndIsReadFalseOrderByCreatedOnDesc(orgId));
    }
    
    @PatchMapping("/organization/{orgId}/notifications/read-all")
    public ResponseEntity<?> markAllRead(@PathVariable Long orgId) {
        var list = notificationRepository
                .findByOrganizationIdAndIsReadFalseOrderByCreatedOnDesc(orgId);
        list.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(list);
        return ResponseEntity.ok("Marked " + list.size() + " as read");
    }
    
    // Manual trigger for testing
    @PostMapping("/admin/expire-points/run-now")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<?> runExpiryNow() {
        scheduler.expireOldPoints();
        return ResponseEntity.ok("Expiry job triggered");
    }
}