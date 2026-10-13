package org.optipace.adminService.repository;

import java.util.Optional;

import org.optipace.adminService.entity.OrganizationPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationPointsRepository extends JpaRepository<OrganizationPoints, Long> {
    
    Optional<OrganizationPoints> findByOrganizationPackageId(Long organizationPackageId);
    
    Optional<OrganizationPoints> findByOrganizationId(Long organizationId);
}