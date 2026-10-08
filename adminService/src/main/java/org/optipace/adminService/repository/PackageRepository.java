package org.optipace.adminService.repository;
import org.optipace.adminService.entity.SubscriptionPackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageRepository extends JpaRepository<SubscriptionPackage, Long> {
    
    boolean existsByPackageName(String packageName);
    
    boolean existsByPackageNameAndPackageIdNot(String packageName, Long packageId);
    
    Page<SubscriptionPackage> findByRecordStatus(Character recordStatus, Pageable pageable);
    
    Page<SubscriptionPackage> findByPackageNameContainingIgnoreCase(String packageName, Pageable pageable);

// ⭐ COUNT METHODS
    
    /** Total count of all packages */
    long countBy();
    
    /** Count only active ('A') or inactive ('I') */
    long countByRecordStatus(Character recordStatus);
    
}