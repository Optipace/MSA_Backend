package org.optipace.masterService.repository;

import org.optipace.masterService.entity.Designation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignationRepository extends JpaRepository<Designation, Long> {
    boolean existsByDesignationCode(String designationCode);
    boolean existsByDesignationCodeAndDesignationIdNot(String designationCode, Long designationId);
    Page<Designation> findByRecordStatusOrderByHierarchyLevelAsc(char recordStatus, Pageable pageable);
}
