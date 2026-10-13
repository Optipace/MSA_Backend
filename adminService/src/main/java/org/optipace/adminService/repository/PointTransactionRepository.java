package org.optipace.adminService.repository;

import org.optipace.adminService.entity.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
    
    List<PointTransaction> findByOrganizationIdOrderByCreatedOnDesc(Long organizationId);
    
    List<PointTransaction> findByOrganizationPackageId(Long organizationPackageId);
    
    List<PointTransaction> findByReferenceTypeAndReferenceId(String referenceType, Long referenceId);
}