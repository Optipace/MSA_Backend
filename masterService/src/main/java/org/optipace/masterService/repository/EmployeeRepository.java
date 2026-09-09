package org.optipace.masterService.repository;

import org.optipace.masterService.entity.Employee;
import org.optipace.masterService.entity.Factory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Page<Employee> findByRecordStatus(Character recordStatus, Pageable pageable);

    boolean existsByEmployeeCode(String employeeCode);

    boolean existsByEmail(String email);
}
