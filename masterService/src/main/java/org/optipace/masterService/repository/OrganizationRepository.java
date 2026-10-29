package org.optipace.masterService.repository;

import org.optipace.masterService.entity.Employee;
import org.optipace.masterService.entity.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    boolean existsByOrganizationCode(String organizationCode);
    boolean existsByGstin(String gstin);
    Page<Organization> findByRecordStatus(char recordStatus, Pageable pageable);
    
    // 👇 Add this native query to get the org ID from the factory admin ID
    @Query(value = "SELECT organization_id FROM master.employee WHERE employee_id = :factoryAdminId", nativeQuery = true)
    Long findOrganizationIdByEmployeeId(@Param("factoryAdminId") Long factoryAdminId);

    
}
