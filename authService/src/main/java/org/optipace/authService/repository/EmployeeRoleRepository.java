package org.optipace.authService.repository;

import org.optipace.authService.entity.EmployeeRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRoleRepository extends JpaRepository<EmployeeRole, Long> {
    Optional<EmployeeRole> findByEmployeeId(Long employeeId);

    Optional<EmployeeRole> findByEmployeeIdAndIsPrimaryRoleTrue(Long employeeId);

    List<EmployeeRole> findByEmployeeIdInAndIsPrimaryRoleTrue(List<Long> employeeIds);

}
