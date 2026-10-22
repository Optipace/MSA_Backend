package org.optipace.adminService.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.optipace.adminService.dto.response.SingleResponse;
import org.optipace.adminService.entity.AssessmentUsage;
import org.optipace.adminService.entity.Organization;
import org.optipace.adminService.entity.OrganizationPackage;
import org.optipace.adminService.entity.OrganizationPoints;
import org.optipace.adminService.entity.PointTransaction;
import org.optipace.adminService.enums.CustomStatus;
import org.optipace.adminService.enums.PointTransactionType;
import org.optipace.adminService.exception.BadRequestException;
import org.optipace.adminService.repository.OrganizationPackageRepository;
import org.optipace.adminService.repository.OrganizationPointsRepository;
import org.optipace.adminService.repository.OrganizationRepository;
import org.optipace.adminService.repository.PointTransactionRepository;
import org.optipace.adminService.service.PointsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointsServiceImpl implements PointsService {
    
    private final OrganizationPointsRepository organizationPointsRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationPackageRepository organizationPackageRepository;
    
    // =====================================================
    // ⭐ AVAILABLE POINTS (excludes expired)
    // =====================================================
    @Override
    public SingleResponse<Map<String, Object>> getAvailablePoints(Long organizationId) {
        
        LocalDate today = LocalDate.now();
        
        // ⭐ 1. Fetch Organization Name
        String organizationName = organizationRepository.findById(organizationId)
                .map(Organization::getOrganizationName)
                .orElse("Unknown Organization");
        
        // 2. Fetch Package Names (Map of organizationPackageId -> packageName)
        List<OrganizationPackage> orgPackages = organizationPackageRepository.findByOrganizationId(organizationId);
        Map<Long, String> packageNamesMap = orgPackages.stream()
                .collect(Collectors.toMap(
                        OrganizationPackage::getOrganizationPackageId,
                        op -> op.getPackageEntity() != null ? op.getPackageEntity().getPackageName() : "Unknown Package",
                        (existing, replacement) -> existing // handle duplicates just in case
                ));
        
        // FIFO available credits
        List<OrganizationPoints> available = organizationPointsRepository
                .findAvailableFifo(organizationId, today);
        
        long totalAvailable = available.stream()
                .mapToLong(OrganizationPoints::getAvailablePoints)
                .sum();
        
        // Points expiring in next 30 days
        LocalDate future = today.plusDays(30);
        List<OrganizationPoints> expiringSoon = organizationPointsRepository
                .findExpiringBetween(today, future).stream()
                .filter(op -> op.getOrganizationId().equals(organizationId))
                .toList();
        
        long expiringIn30Days = expiringSoon.stream()
                .mapToLong(OrganizationPoints::getAvailablePoints)
                .sum();
        
        // Expired but not yet zeroed (shouldn't count in total)
        List<OrganizationPoints> allForOrg = organizationPointsRepository
                .findByOrganizationId(organizationId);
        
        long expiredUnused = allForOrg.stream()
                .filter(op -> Boolean.TRUE.equals(op.getIsExpired())
                        || (op.getExpiryDate() != null 
                            && op.getExpiryDate().isBefore(today)))
                .mapToLong(OrganizationPoints::getAvailablePoints)
                .sum();
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("organizationId", organizationId);
        result.put("organizationName", organizationName); // ⭐ ADDED HERE
        result.put("totalAvailable", totalAvailable);
        result.put("expiringIn30Days", expiringIn30Days);
        result.put("expiredUnused", expiredUnused);
        result.put("breakdown", available.stream().map(op -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("pointsId", op.getPointsId());
            item.put("organizationPackageId", op.getOrganizationPackageId());
            item.put("packageName", packageNamesMap.getOrDefault(op.getOrganizationPackageId(), "Unknown Package"));
            item.put("totalPoints", op.getTotalPoints());
            item.put("usedPoints", op.getUsedPoints());
            item.put("availablePoints", op.getAvailablePoints());
            item.put("expiryDate", op.getExpiryDate());
            return item;
        }).toList());
        
        return new SingleResponse<>(result, CustomStatus.SUCCESS);
    }
    
    // =====================================================
    // ⭐ CONSUME POINTS — FIFO from oldest non-expired
    // =====================================================
    @Override
    @Transactional
    public void consumePoints(Long organizationId, Long pointsToConsume,
                              String referenceType, Long referenceId, Long userId) {
        
        log.info("Consuming {} points for org {} (ref: {}:{})",
                 pointsToConsume, organizationId, referenceType, referenceId);
        
        LocalDate today = LocalDate.now();
        
        // 1. Get FIFO list of available credits
        List<OrganizationPoints> credits = organizationPointsRepository
                .findAvailableFifo(organizationId, today);
        
        long remainingToConsume = pointsToConsume;
        long totalConsumed = 0;
        
        // 2. Consume FIFO
        for (OrganizationPoints credit : credits) {
            if (remainingToConsume <= 0) break;
            
            long available = credit.getAvailablePoints();
            if (available <= 0) continue;
            
            long consume = Math.min(available, remainingToConsume);
            
            credit.setUsedPoints(credit.getUsedPoints() + consume);
            credit.setAvailablePoints(available - consume);
            
            if (credit.getAvailablePoints() == 0) {
                credit.setIsExpired(true);       // mark closed
            }
            
            organizationPointsRepository.save(credit);
            remainingToConsume -= consume;
            totalConsumed += consume;
            
            log.debug("Consumed {} from pointsId {} (remaining: {})",
                     consume, credit.getPointsId(), credit.getAvailablePoints());
        }
        
        if (remainingToConsume > 0) {
            throw new BadRequestException(
                "Insufficient points. Required: " + pointsToConsume
                + ", available: " + totalConsumed);
        }
        
        // 3. Ledger entry
        PointTransaction debit = PointTransaction.builder()
            .organizationId(organizationId)
            .transactionType(PointTransactionType.DEBIT)
            .points(-pointsToConsume)
            .referenceType(referenceType)
            .referenceId(referenceId)
            .description("Points consumed for " + referenceType + " #" + referenceId)
            .createdBy(userId)
            .build();
        
        pointTransactionRepository.save(debit);
        log.info("Consumed {} points (org: {})", pointsToConsume, organizationId);
    }
    
    // =====================================================
    // ⭐ CREDIT POINTS (called from purchase callback)
    // =====================================================
    @Override
    @Transactional
    public void creditPoints(Long organizationId, Long organizationPackageId,
                             Long points, LocalDate expiryDate, Long userId) {
        
        log.info("Crediting {} points to org {} (pkg: {}, expires: {})",
                 points, organizationId, organizationPackageId, expiryDate);
        
        // 1. Upsert organization_points
        OrganizationPoints op = organizationPointsRepository
                .findByOrganizationPackageId(organizationPackageId)
                .orElseGet(() -> OrganizationPoints.builder()
                        .organizationId(organizationId)
                        .organizationPackageId(organizationPackageId)
                        .totalPoints(0L)
                        .usedPoints(0L)
                        .availablePoints(0L)
                        .expiryDate(expiryDate)
                        .isExpired(false)
                        .build());
        
        // ⭐ Add to existing (in case of renewal)
        op.setTotalPoints(op.getTotalPoints() + points);
        op.setAvailablePoints(op.getAvailablePoints() + points);
        op.setExpiryDate(expiryDate);              // update expiry
        op.setIsExpired(false);                    // reactivate
        
        organizationPointsRepository.save(op);
        
        // 2. Ledger entry
        PointTransaction credit = PointTransaction.builder()
            .organizationId(organizationId)
            .transactionType(PointTransactionType.CREDIT)
            .points(points)
            .referenceType("PURCHASE")
            .referenceId(organizationPackageId)
            .description("Points credited for package purchase (expires: " + expiryDate + ")")
            .createdBy(userId)
            .build();
        
        pointTransactionRepository.save(credit);
        
        log.info("Points credited: {} (expires: {})", points, expiryDate);
    }
}