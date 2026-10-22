package org.optipace.adminService.repository;

import org.optipace.adminService.entity.OrganizationPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationPointsRepository extends JpaRepository<OrganizationPoints, Long> {
    
    Optional<OrganizationPoints> findByOrganizationPackageId(Long organizationPackageId);
    
    // ⭐ FIFO — oldest non-expired packages first
    @Query("""
        SELECT op FROM OrganizationPoints op
        WHERE op.organizationId = :orgId
          AND op.availablePoints > 0
          AND op.isExpired = false
          AND (op.expiryDate IS NULL OR op.expiryDate >= :today)
        ORDER BY op.createdOn ASC
    """)
    List<OrganizationPoints> findAvailableFifo(
            @Param("orgId") Long orgId,
            @Param("today") LocalDate today);
    
    // ⭐ All points for an org (for total count)
    List<OrganizationPoints> findByOrganizationId(Long organizationId);
    
    // ⭐ Expired but not yet marked (for scheduled job)
    @Query("""
        SELECT op FROM OrganizationPoints op
        WHERE op.availablePoints > 0
          AND op.isExpired = false
          AND op.expiryDate IS NOT NULL
          AND op.expiryDate < :today
    """)
    List<OrganizationPoints> findExpiredNotMarked(@Param("today") LocalDate today);
    
    // ⭐ Expiring soon (for warning notifications)
    @Query("""
        SELECT op FROM OrganizationPoints op
        WHERE op.availablePoints > 0
          AND op.isExpired = false
          AND op.expiryDate BETWEEN :today AND :futureDate
    """)
    List<OrganizationPoints> findExpiringBetween(
            @Param("today") LocalDate today,
            @Param("futureDate") LocalDate futureDate);
}