package org.optipace.adminService.repository;

import org.optipace.adminService.entity.OrganizationPackage;
import org.optipace.adminService.enums.PaymentStatus;
import org.optipace.adminService.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationPackageRepository 
        extends JpaRepository<OrganizationPackage, Long> {
    
    Optional<OrganizationPackage> findByTransactionId(String transactionId);
    
    List<OrganizationPackage> findByFactoryId(Long factoryId);
    
    List<OrganizationPackage> findByOrganizationId(Long organizationId);
    
    List<OrganizationPackage> findByPaymentStatus(PaymentStatus status);
    
    List<OrganizationPackage> findBySubscriptionStatus(SubscriptionStatus status);
    
    // ⭐ Find existing active subscription for org + factory + package
    Optional<OrganizationPackage> findFirstByOrganizationIdAndFactoryIdAndPackageEntity_PackageIdAndSubscriptionStatusAndEndDateGreaterThanEqual(
        Long organizationId,
        Long factoryId,
        Long packageId,
        SubscriptionStatus status,
        LocalDate today
    );
    
    // ⭐ Alternative: any active subscription for org + factory (regardless of package)
    Optional<OrganizationPackage> findFirstByOrganizationIdAndFactoryIdAndSubscriptionStatusAndEndDateGreaterThanEqual(
        Long organizationId,
        Long factoryId,
        SubscriptionStatus status,
        LocalDate today
    );
    
    // ⭐ All active subscriptions for org + factory
    List<OrganizationPackage> findByOrganizationIdAndFactoryIdAndSubscriptionStatus(
        Long organizationId,
        Long factoryId,
        SubscriptionStatus status
    );
    
    // ⭐ Latest subscription regardless of status
    Optional<OrganizationPackage> findFirstByOrganizationIdAndFactoryIdOrderByCreatedOnDesc(
        Long organizationId,
        Long factoryId
    );
    
}