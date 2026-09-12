package org.optipace.masterService.repository;

import org.optipace.masterService.entity.Section;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    Page<Section> findByRecordStatus(char recordStatus, Pageable pageable);

    boolean existsBySectionCode(String sectionCode);

    boolean existsByFactory_FactoryIdAndDepartment_DepartmentIdAndSectionCodeAndSectionIdNot(
            Long factoryId, Long departmentId, String sectionCode, Long sectionId);

}
