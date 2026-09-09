package org.optipace.masterService.repository;

import org.optipace.masterService.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByDepartmentCode(String departmentCode);
    boolean existsByDepartmentCodeAndDepartmentIdNot(String DepartmentCode, Long DepartmentId);
    Page<Department> findByRecordStatus(Character recordStatus, Pageable pageable);
}
