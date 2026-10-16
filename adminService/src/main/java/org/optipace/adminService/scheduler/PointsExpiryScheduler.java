package org.optipace.adminService.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.optipace.adminService.entity.Notification;
import org.optipace.adminService.entity.OrganizationPoints;
import org.optipace.adminService.entity.PointTransaction;
import org.optipace.adminService.enums.PointTransactionType;
import org.optipace.adminService.repository.NotificationRepository;
import org.optipace.adminService.repository.OrganizationPointsRepository;
import org.optipace.adminService.repository.PointTransactionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointsExpiryScheduler {
    
    private final OrganizationPointsRepository organizationPointsRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final NotificationRepository notificationRepository;
    
    // =====================================================
    // ⭐ DAILY 1 AM — expire old points + notify
    // =====================================================
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void expireOldPoints() {
        
        LocalDate today = LocalDate.now();
        log.info("▶ Points expiry job started for {}", today);
        
        // 1. Find all points rows that expired but aren't marked
        List<OrganizationPoints> expired = organizationPointsRepository
                .findExpiredNotMarked(today);
        
        if (expired.isEmpty()) {
            log.info("No expired points to process");
            return;
        }
        
        // 2. Group by organization
        Map<Long, List<OrganizationPoints>> byOrg = expired.stream()
                .collect(Collectors.groupingBy(OrganizationPoints::getOrganizationId));
        
        for (Map.Entry<Long, List<OrganizationPoints>> entry : byOrg.entrySet()) {
            
            Long organizationId = entry.getKey();
            List<OrganizationPoints> rows = entry.getValue();
            
            long totalExpired = 0;
            List<Long> packageIds = new ArrayList<>();
            
            for (OrganizationPoints op : rows) {
                long remaining = op.getAvailablePoints();
                if (remaining <= 0) continue;
                
                totalExpired += remaining;
                packageIds.add(op.getOrganizationPackageId());
                
                // Zero out and mark expired
                op.setUsedPoints(op.getUsedPoints() + remaining);   // account for expiry
                op.setAvailablePoints(0L);
                op.setIsExpired(true);
                organizationPointsRepository.save(op);
            }
            
            // 3. Ledger entry
            if (totalExpired > 0) {
                PointTransaction expiry = PointTransaction.builder()
                    .organizationId(organizationId)
                    .transactionType(PointTransactionType.EXPIRY)
                    .points(-totalExpired)
                    .referenceType("EXPIRY")
                    .description("Points expired for packages: " + packageIds)
                    .createdBy(1L)
                    .build();
                pointTransactionRepository.save(expiry);
            }
            
            // 4. Notify admin
            Notification notif = Notification.builder()
                .organizationId(organizationId)
                .title("Points Expired")
                .message(String.format(
                    "%d points expired from package(s): %s. " +
                    "These points were valid until their package end date and are no longer usable.",
                    totalExpired, packageIds))
                .notificationType("POINTS_EXPIRED")
                .referenceType("ORGANIZATION_PACKAGE")
                .referenceId(packageIds.isEmpty() ? null : packageIds.get(0))
                .isRead(false)
                .build();
            notificationRepository.save(notif);
            
            log.info("Expired {} points for org {} (packages: {})",
                     totalExpired, organizationId, packageIds);
        }
        
        log.info("✔ Expiry job complete. {} orgs notified.", byOrg.size());
    }
    
    // =====================================================
    // ⭐ DAILY 9 AM — warn about points expiring in 30 days
    // =====================================================
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void notifyExpiringSoon() {
        
        LocalDate today = LocalDate.now();
        LocalDate future = today.plusDays(30);
        
        List<OrganizationPoints> expiring = organizationPointsRepository
                .findExpiringBetween(today, future);
        
        Map<Long, List<OrganizationPoints>> byOrg = expiring.stream()
                .collect(Collectors.groupingBy(OrganizationPoints::getOrganizationId));
        
        for (Map.Entry<Long, List<OrganizationPoints>> entry : byOrg.entrySet()) {
            
            long total = entry.getValue().stream()
                    .mapToLong(OrganizationPoints::getAvailablePoints)
                    .sum();
            
            if (total == 0) continue;
            
            Notification notif = Notification.builder()
                .organizationId(entry.getKey())
                .title("Points Expiring Soon")
                .message(String.format(
                    "%d points are expiring within 30 days. Use them before they expire.",
                    total))
                .notificationType("POINTS_EXPIRING")
                .isRead(false)
                .build();
            notificationRepository.save(notif);
        }
        
        log.info("Notified {} orgs about expiring points", byOrg.size());
    }
}