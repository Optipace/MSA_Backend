package org.optipace.masterService.repository;

import org.optipace.masterService.entity.Factory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactoryRepository extends JpaRepository<Factory, Long> {
    Page<Factory> findByRecordStatus(Character recordStatus, Pageable pageable);
    boolean existsByFactoryCode(String factoryCode);
    boolean existsByFactoryCodeAndOrganization_OrganizationIdAndFactoryIdNot(
            String factoryCode,
            Long organizationId,
            Long factoryId
    );
}
