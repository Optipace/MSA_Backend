package org.optipace.adminService.repository;

import org.optipace.adminService.entity.OrganizationPackage;
import org.optipace.adminService.enums.PaymentStatus;
import org.optipace.adminService.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}