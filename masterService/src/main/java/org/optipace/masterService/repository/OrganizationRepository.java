package org.optipace.masterService.repository;

import org.optipace.masterService.entity.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    boolean existsByOrganizationCode(String organizationCode);
    boolean existsByGstin(String gstin);
    Page<Organization> findByRecordStatus(char recordStatus, Pageable pageable);
}
